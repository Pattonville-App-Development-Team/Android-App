package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.EmptyValidator;
import net.fortuna.ical4j.validate.component.VEventAddValidator;
import net.fortuna.ical4j.validate.component.VEventCancelValidator;
import net.fortuna.ical4j.validate.component.VEventCounterValidator;
import net.fortuna.ical4j.validate.component.VEventDeclineCounterValidator;
import net.fortuna.ical4j.validate.component.VEventPublishValidator;
import net.fortuna.ical4j.validate.component.VEventRefreshValidator;
import net.fortuna.ical4j.validate.component.VEventReplyValidator;
import net.fortuna.ical4j.validate.component.VEventRequestValidator;

import org.apache.commons.lang3.time.StopWatch;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import de.javakaffee.kryoserializers.EnumMapSerializer;
import de.javakaffee.kryoserializers.URISerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;

/**
 * Created by Mitchell on 12/24/2016.
 * <p>
 * This class implements the asynchronous parsing of a downloaded iCal file.
 *
 * @author Mitchell Skaggs
 */

public class CalendarDownloadAndParseTask extends AsyncTask<Set<DataSource>, Double, CalendarData> {

    private static final String TAG = "CalendarDPTask";
    private static final String FILENAME = "calendar_data.bin";
    private final CalendarFragment calendarFragment;
    private final RequestQueue requestQueue;
    private final NetworkInfo activeNetwork;
    private final Kryo kryo;

    public CalendarDownloadAndParseTask(CalendarFragment calendarFragment, RequestQueue requestQueue) {
        this.calendarFragment = calendarFragment;
        this.requestQueue = requestQueue;
        ConnectivityManager cm = (ConnectivityManager) calendarFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        this.kryo = new Kryo();
        this.kryo.setRegistrationRequired(true);

        registerKryoClasses(this.kryo);
    }

    private static void registerKryoClasses(Kryo kryo) {
        kryo.register(CalendarData.class, new Serializer<CalendarData>() {
            @Override
            public void write(Kryo kryo, Output output, CalendarData object) {
                kryo.writeObject(output, object.getCalendars());
            }

            @Override
            public CalendarData read(Kryo kryo, Input input, Class<CalendarData> type) {
                //noinspection unchecked
                return new CalendarData(kryo.readObject(input, EnumMap.class));
            }
        });
        kryo.register(EnumMap.class, new EnumMapSerializer());
        kryo.register(DataSource.class);
        kryo.register(HashMultimap.class, new HashMultimapSerializer());
        kryo.register(SerializableCalendarDay.class, new Serializer<SerializableCalendarDay>() {
            @Override
            public void write(Kryo kryo, Output output, SerializableCalendarDay object) {
                kryo.writeObject(output, object.getCalendarDay());
            }

            @Override
            public SerializableCalendarDay read(Kryo kryo, Input input, Class<SerializableCalendarDay> type) {
                return SerializableCalendarDay.of(kryo.readObject(input, CalendarDay.class));
            }
        });
        kryo.register(CalendarDay.class, new Serializer<CalendarDay>() {
            @Override
            public void write(Kryo kryo, Output output, CalendarDay object) {
                kryo.writeObject(output, object.getYear());
                kryo.writeObject(output, object.getMonth());
                kryo.writeObject(output, object.getDay());
            }

            @Override
            public CalendarDay read(Kryo kryo, Input input, Class<CalendarDay> type) {
                return CalendarDay.from(kryo.readObject(input, int.class), kryo.readObject(input, int.class), kryo.readObject(input, int.class));
            }
        });
        kryo.register(HashSet.class);
        kryo.register(VEvent.class, new Serializer<VEvent>() {
            @Override
            public void write(Kryo kryo, Output output, VEvent object) {
                kryo.writeObject(output, object.getProperties());
                kryo.writeObject(output, object.getAlarms());
            }

            @Override
            public VEvent read(Kryo kryo, Input input, Class<VEvent> type) {
                //noinspection unchecked
                return new VEvent(kryo.readObject(input, PropertyList.class), ((ComponentList<VAlarm>) kryo.readObject(input, ComponentList.class)));
            }
        });
        kryo.register(ComponentList.class);
        kryo.register(HashMap.class);
        kryo.register(Method.ADD.getClass(), new Serializer<Method>() {
            @Override
            public void write(Kryo kryo, Output output, Method object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Method read(Kryo kryo, Input input, Class<Method> type) {
                ParameterList parameterList = kryo.readObject(input, ParameterList.class);
                String value = kryo.readObject(input, String.class);
                switch (value) {
                    case "PUBLISH":
                        return Method.PUBLISH;
                    case "REPLY":
                        return Method.REPLY;
                    case "ADD":
                        return Method.ADD;
                    case "CANCEL":
                        return Method.CANCEL;
                    case "REFRESH":
                        return Method.REFRESH;
                    case "COUNTER":
                        return Method.COUNTER;
                    case "DECLINE-COUNTER":
                        return Method.DECLINE_COUNTER;
                    default:
                        return new Method(parameterList, value);
                }
            }
        });
        kryo.register(PropertyFactoryImpl.class);
        kryo.register(ParameterList.class);
        kryo.register(Collections.EMPTY_LIST.getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
        kryo.register(EmptyValidator.class);
        kryo.register(VEventRequestValidator.class);
        kryo.register(VEventReplyValidator.class);
        kryo.register(VEventCounterValidator.class);
        kryo.register(VEventDeclineCounterValidator.class);
        kryo.register(VEventPublishValidator.class);
        kryo.register(VEventAddValidator.class);
        kryo.register(VEventCancelValidator.class);
        kryo.register(VEventRefreshValidator.class);
        kryo.register(PropertyList.class, new CollectionSerializer());
        kryo.register(LastModified.class, new Serializer<LastModified>() {
            @Override
            public void write(Kryo kryo, Output output, LastModified object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getDateTime());
            }

            @Override
            public LastModified read(Kryo kryo, Input input, Class<LastModified> type) {
                return new LastModified(kryo.readObject(input, ParameterList.class), kryo.readObject(input, DateTime.class));
            }
        });
        kryo.register(DateTime.class, new Serializer<DateTime>() {
            @Override
            public void write(Kryo kryo, Output output, DateTime object) {
                kryo.writeObject(output, object.getTime());
            }

            @Override
            public DateTime read(Kryo kryo, Input input, Class<DateTime> type) {
                return new DateTime(kryo.readObject(input, long.class));
            }
        });
        kryo.register(CopyOnWriteArrayList.class);
        kryo.register(Value.class, new Serializer<Value>() {
            @Override
            public void write(Kryo kryo, Output output, Value object) {
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Value read(Kryo kryo, Input input, Class<Value> type) {
                return new Value(kryo.readObject(input, String.class));
            }
        });
        kryo.register(ParameterFactoryImpl.class);
        kryo.register(DtStart.class, new Serializer<DtStart>() {
            @Override
            public void write(Kryo kryo, Output output, DtStart object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getDate());
            }

            @Override
            public DtStart read(Kryo kryo, Input input, Class<DtStart> type) {
                return new DtStart(kryo.readObject(input, ParameterList.class), kryo.readObject(input, Date.class));
            }
        });
        kryo.register(Duration.class, new Serializer<Duration>() {
            @Override
            public void write(Kryo kryo, Output output, Duration object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getDuration());
            }

            @Override
            public Duration read(Kryo kryo, Input input, Class<Duration> type) {
                return new Duration(kryo.readObject(input, ParameterList.class), kryo.readObject(input, Dur.class));
            }
        });
        kryo.register(Dur.class, new Serializer<Dur>() {
            @Override
            public void write(Kryo kryo, Output output, Dur object) {
                kryo.writeObject(output, object.toString());
            }

            @Override
            public Dur read(Kryo kryo, Input input, Class<Dur> type) {
                return new Dur(kryo.readObject(input, String.class));
            }
        });
        kryo.register(Organizer.class, new Serializer<Organizer>() {
            @Override
            public void write(Kryo kryo, Output output, Organizer object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getCalAddress());
            }

            @Override
            public Organizer read(Kryo kryo, Input input, Class<Organizer> type) {
                return new Organizer(kryo.readObject(input, ParameterList.class), kryo.readObject(input, URI.class));
            }
        });
        kryo.register(URI.class, new URISerializer());
        kryo.register(Cn.class, new Serializer<Cn>() {
            @Override
            public void write(Kryo kryo, Output output, Cn object) {
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Cn read(Kryo kryo, Input input, Class<Cn> type) {
                return new Cn(kryo.readObject(input, String.class));
            }
        });
        kryo.register(Summary.class, new Serializer<Summary>() {
            @Override
            public void write(Kryo kryo, Output output, Summary object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Summary read(Kryo kryo, Input input, Class<Summary> type) {
                return new Summary(kryo.readObject(input, ParameterList.class), kryo.readObject(input, String.class));
            }
        });
        kryo.register(Location.class, new Serializer<Location>() {
            @Override
            public void write(Kryo kryo, Output output, Location object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Location read(Kryo kryo, Input input, Class<Location> type) {
                return new Location(kryo.readObject(input, ParameterList.class), kryo.readObject(input, String.class));
            }
        });
        kryo.register(Transp.TRANSPARENT.getClass(), new Serializer<Transp>() {
            @Override
            public void write(Kryo kryo, Output output, Transp object) {
                switch (object.getValue()) {
                    case "OPAQUE":
                        kryo.writeObject(output, 0);
                        break;
                    case "TRANSPARENT":
                        kryo.writeObject(output, 1);
                        break;
                }
            }

            @Override
            public Transp read(Kryo kryo, Input input, Class<Transp> type) {
                switch (kryo.readObject(input, int.class)) {
                    case 0:
                        return Transp.OPAQUE;
                    case 1:
                        return Transp.TRANSPARENT;
                }
                return null;
            }
        });
        kryo.register(Uid.class, new Serializer<Uid>() {
            @Override
            public void write(Kryo kryo, Output output, Uid object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Uid read(Kryo kryo, Input input, Class<Uid> type) {
                return new Uid(kryo.readObject(input, ParameterList.class), kryo.readObject(input, String.class));
            }
        });
        kryo.register(Categories.class, new Serializer<Categories>() {
            @Override
            public void write(Kryo kryo, Output output, Categories object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getCategories());
            }

            @Override
            public Categories read(Kryo kryo, Input input, Class<Categories> type) {
                return new Categories(kryo.readObject(input, ParameterList.class), kryo.readObject(input, TextList.class));
            }
        });
        kryo.register(TextList.class, new Serializer<TextList>() {
            @Override
            public void write(Kryo kryo, Output output, TextList object) {
                kryo.writeObject(output, Iterators.toArray(object.iterator(), String.class));
            }

            @Override
            public TextList read(Kryo kryo, Input input, Class<TextList> type) {
                return new TextList(kryo.readObject(input, String[].class));
            }
        });
        kryo.register(Description.class, new Serializer<Description>() {
            @Override
            public void write(Kryo kryo, Output output, Description object) {

                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public Description read(Kryo kryo, Input input, Class<Description> type) {
                return new Description(kryo.readObject(input, ParameterList.class), kryo.readObject(input, String.class));
            }
        });
        kryo.register(Url.class, new Serializer<Url>() {
            @Override
            public void write(Kryo kryo, Output output, Url object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getUri());
            }

            @Override
            public Url read(Kryo kryo, Input input, Class<Url> type) {
                return new Url(kryo.readObject(input, ParameterList.class), kryo.readObject(input, URI.class));
            }
        });
        kryo.register(RRule.class, new Serializer<RRule>() {
            @Override
            public void write(Kryo kryo, Output output, RRule object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getRecur());
            }

            @Override
            public RRule read(Kryo kryo, Input input, Class<RRule> type) {
                return new RRule(kryo.readObject(input, ParameterList.class), kryo.readObject(input, Recur.class));
            }
        });
        kryo.register(Recur.class);
        kryo.register(WeekDayList.class);
        kryo.register(NumberList.class);
        kryo.register(WeekDay.Day.class);
        kryo.register(WeekDay.class, new Serializer<WeekDay>() {
            @Override
            public void write(Kryo kryo, Output output, WeekDay object) {
                kryo.writeObject(output, object.getDay());
            }

            @Override
            public WeekDay read(Kryo kryo, Input input, Class<WeekDay> type) {
                return WeekDay.getWeekDay(kryo.readObject(input, WeekDay.Day.class));
            }
        });
        kryo.register(ArrayList.class);
        kryo.register(String[].class);
        kryo.register(Date.class);
    }

    private static String fixICalStrings(String iCalString) {
        return iCalString.replace("FREQ=;", "FREQ=YEARLY;");
    }

    private HashMultimap<SerializableCalendarDay, VEvent> parseFile(String iCalFile) {
        Log.d(TAG, "Initial");
        HashMultimap<SerializableCalendarDay, VEvent> map = HashMultimap.create();//HashMultimap.multiValueMap(new HashMap<SerializableCalendarDay, HashSet<VEvent>>(), new SetFactories.HashSetVEventFactory());
        StringReader stringReader = new StringReader(fixICalStrings(iCalFile));
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = null;
        Log.d(TAG, "Readers done");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            calendar = calendarBuilder.build(stringReader);
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        Log.d(TAG, "Calendar build time: " + stopWatch.getTime() + "ms");
        Log.d(TAG, Runtime.getRuntime().availableProcessors() + " possible simultaneous tasks with " + Runtime.getRuntime().maxMemory() + " bytes of memory max and " + Runtime.getRuntime().freeMemory() + " bytes of memory free");
        if (calendar != null) {
            Set<VEvent> vEventSet = Stream.of(calendar.getComponents()).filter(new Predicate<CalendarComponent>() {
                @Override
                public boolean test(CalendarComponent value) {
                    return value instanceof VEvent;
                }
            }).map(new Function<CalendarComponent, VEvent>() {
                @Override
                public VEvent apply(CalendarComponent value) {
                    return (VEvent) value;
                }
            }).collect(Collectors.<VEvent>toSet());
            Log.d(TAG, "Set built");
            for (VEvent vEvent : vEventSet) {
                Date vEventDate = vEvent.getStartDate().getDate();
                map.put(SerializableCalendarDay.of(CalendarDay.from(vEventDate)), vEvent);
            }
            Log.d(TAG, "Map built");
        }
        return map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "OnPreExecute called");
        calendarFragment.showProgressBar();
        calendarFragment.getProgressBar().setIndeterminate(false);
        calendarFragment.getProgressBar().setMax(100);
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "OnProgressUpdate called: " + (float) (100 * values[0]) + "%");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            calendarFragment.getProgressBar().setProgress((int) (100 * values[0]), true);
        else
            calendarFragment.getProgressBar().setProgress((int) (100 * values[0]));

    }

    @Override
    protected void onPostExecute(CalendarData calendarData) {
        super.onPostExecute(calendarData);
        Log.d(TAG, "OnPostExecute called");
        calendarFragment.setCalendarData(calendarData);
        calendarFragment.hideProgressBar();
    }

    @Override
    protected void onCancelled(CalendarData calendarData) {
        super.onCancelled(calendarData);
        Log.d(TAG, "OnCancelled called");
        calendarFragment.hideProgressBar();
    }

    @SafeVarargs
    @Override
    protected final CalendarData doInBackground(Set<DataSource>... params) {
        if (params.length != 1)
            throw new IllegalStateException("Only one parameter expected!");
        Set<DataSource> param = params[0];
        EnumMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> results = new EnumMap<>(DataSource.class);

        File calendarDataCache = new File(calendarFragment.getActivity().getCacheDir(), FILENAME);
        if (calendarDataCache.exists()
                && TimeUnit.HOURS.convert(System.currentTimeMillis() - calendarDataCache.lastModified(), TimeUnit.MILLISECONDS) < 48 //Time before cache refresh
                ) {
            Log.i(TAG, "Loading serialized calendar cache, " + (calendarDataCache.length() / 1024) + " KB, " + (System.currentTimeMillis() - calendarDataCache.lastModified()) + "ms old");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //A cache exists
            FileInputStream fileInputStream = null;
            Input input = null;
            try {
                fileInputStream = new FileInputStream(calendarDataCache);
                input = new Input(fileInputStream);
                CalendarData cachedCalendarData = kryo.readObject(input, CalendarData.class);

                for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : cachedCalendarData.getCalendars().entrySet())
                    if (param.contains(entry.getKey()) && entry.getValue().size() != 0)
                        results.put(entry.getKey(), entry.getValue());

            } catch (FileNotFoundException e) {
                //Proceed to the download if failure
                e.printStackTrace();
            } catch (KryoException | IndexOutOfBoundsException e) {
                //If Kryo can't load the file
                if (calendarDataCache.delete()) {
                    Log.d(TAG, "Corrupted cache file deleted");
                    e.printStackTrace();
                } else {
                    Log.d(TAG, "Failed to delete corrupt cache!");
                    e.printStackTrace();
                }
            } finally { //Prevent any resource leaks
                if (input != null) {
                    try {
                        input.close();
                    } catch (KryoException e) {
                        e.printStackTrace();
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            stopWatch.stop();
            Log.i(TAG, "Loaded calendar data in " + stopWatch.getTime() + "ms");
        } else {
            Log.i(TAG, "Calendar cache does not exist or is expired, redownloading");
        }

        //Remove duplicate DataSources
        int initialParamSize = param.size(); //Keep the initial value to start the progress bar from
        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : results.entrySet())
            param.remove(entry.getKey());
        int removed = initialParamSize - param.size(); //How many were reused

        //The data that is not cached
        Map<DataSource, String> downloadMap = new EnumMap<>(DataSource.class);
        try {
            downloadMap = downloadFiles(param);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (isCancelled())
            return null;

        int i = removed; //Start mid-way through downloads
        for (DataSource dataSource : param) {
            results.put(dataSource, parseFile(downloadMap.get(dataSource)));
            publishProgress((double) (i++ + 1) / initialParamSize);

            //Break early if cancelled
            if (isCancelled())
                return new CalendarData(results);
        }
        CalendarData calendarData = new CalendarData(results);
        FileOutputStream fileOutputStream = null;
        Output output = null;
        try {
            fileOutputStream = new FileOutputStream(calendarDataCache);
            output = new Output(fileOutputStream);
            kryo.writeObject(output, calendarData);

            if (!calendarDataCache.setLastModified(System.currentTimeMillis()))
                Log.e(TAG, "Failed to set last modified time!");
        } catch (IOException e) {
            Log.e(TAG, "Error writing file!!");
            e.printStackTrace();
        } finally { //Prevent any resource leaks
            if (output != null) {
                try {
                    output.close();
                } catch (KryoException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return calendarData;
    }

    private Map<DataSource, String> downloadFiles(Set<DataSource> dataSources) throws ExecutionException, InterruptedException {
        Cache cache = requestQueue.getCache();

        Map<DataSource, RequestFuture<String>> requests = new EnumMap<>(DataSource.class);
        Map<DataSource, String> results = new EnumMap<>(DataSource.class);

        for (DataSource dataSource : dataSources) {
            Cache.Entry entry = cache.get(dataSource.calendarURL);
            if (entry != null)
                Log.d(TAG, "Cache of " + dataSource.name + " isExpired: " + entry.isExpired() + " refreshNeeded: " + entry.refreshNeeded() + " responseHeaders: " + entry.responseHeaders);
            if (entry != null && !entry.isExpired()) { //No download needed
                results.put(dataSource, new String(entry.data));
                Log.d(TAG, "Cached " + dataSource.name);
            } else { //Needs to be downloaded
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { //Hope for download
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(dataSource.calendarURL, future, future);
                    requestQueue.add(request);
                    requests.put(dataSource, future);
                    Log.d(TAG, "Downloading " + dataSource.name);
                } else if (entry != null) { //No hope for download; if cached but out of date, use instead
                    results.put(dataSource, new String(entry.data));
                    Log.i(TAG, "Using old cache due to no Internet connection for " + dataSource.name);
                }
            }
        }

        int i = 0;
        for (Map.Entry<DataSource, RequestFuture<String>> entry : requests.entrySet()) {
            String result = entry.getValue().get();
            if (result != null)
                results.put(entry.getKey(), result);
            else
                Log.i(TAG, "Result was null for " + entry.getKey().name);
            if (isCancelled())
                return results;
            publishProgress((double) (i++ + 1) / dataSources.size());
        }

        return results;
    }

}