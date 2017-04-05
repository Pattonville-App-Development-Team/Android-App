package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.prolificinteractive.materialcalendarview.CalendarDay;

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
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.XComponent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
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
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.validate.EmptyValidator;
import net.fortuna.ical4j.validate.component.VEventAddValidator;
import net.fortuna.ical4j.validate.component.VEventCancelValidator;
import net.fortuna.ical4j.validate.component.VEventCounterValidator;
import net.fortuna.ical4j.validate.component.VEventDeclineCounterValidator;
import net.fortuna.ical4j.validate.component.VEventPublishValidator;
import net.fortuna.ical4j.validate.component.VEventRefreshValidator;
import net.fortuna.ical4j.validate.component.VEventReplyValidator;
import net.fortuna.ical4j.validate.component.VEventRequestValidator;

import org.pattonvillecs.pattonvilleapp.DataSource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javakaffee.kryoserializers.EnumMapSerializer;
import de.javakaffee.kryoserializers.URISerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;

/**
 * Created by Mitchell Skaggs on 1/25/2017.
 */
public final class KryoUtil {
    private KryoUtil() {
    }

    public static Kryo registerKryoClasses(Kryo kryo) {
        kryo.register(EnumMap.class, new EnumMapSerializer());
        kryo.register(DataSource.class);
        kryo.register(HashMultimap.class, new HashMultimapSerializer());
        kryo.register(UtcOffset.class, new Serializer<UtcOffset>() {
            @Override
            public void write(Kryo kryo, Output output, UtcOffset object) {
                kryo.writeObject(output, object.getOffset());
            }

            @Override
            public UtcOffset read(Kryo kryo, Input input, Class<UtcOffset> type) {
                return new UtcOffset(kryo.readObject(input, Long.class));
            }
        });
        kryo.register(TzId.class, new Serializer<TzId>() {
            @Override
            public void write(Kryo kryo, Output output, TzId object) {
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public TzId read(Kryo kryo, Input input, Class<TzId> type) {
                return new TzId(kryo.readObject(input, String.class));
            }
        });
        kryo.register(TimeZone.class, new Serializer<TimeZone>() {
            @Override
            public void write(Kryo kryo, Output output, TimeZone object) {
                kryo.writeObject(output, object.getVTimeZone());
            }

            @Override
            public TimeZone read(Kryo kryo, Input input, Class<TimeZone> type) {
                return new TimeZone(kryo.readObject(input, VTimeZone.class));
            }
        });
        kryo.register(Daylight.class, new Serializer<Daylight>() {
            @Override
            public void write(Kryo kryo, Output output, Daylight object) {
                kryo.writeObject(output, object.getProperties());
            }

            @Override
            public Daylight read(Kryo kryo, Input input, Class<Daylight> type) {
                return new Daylight(kryo.readObject(input, PropertyList.class));
            }
        });
        kryo.register(Standard.class, new Serializer<Standard>() {
            @Override
            public void write(Kryo kryo, Output output, Standard object) {
                kryo.writeObject(output, object.getProperties());
            }

            @Override
            public Standard read(Kryo kryo, Input input, Class<Standard> type) {
                return new Standard(kryo.readObject(input, PropertyList.class));
            }
        });
        kryo.register(XProperty.class, new Serializer<XProperty>() {
            @Override
            public void write(Kryo kryo, Output output, XProperty object) {
                kryo.writeObject(output, object.getName());
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getValue());
            }

            @Override
            public XProperty read(Kryo kryo, Input input, Class<XProperty> type) {
                return new XProperty(kryo.readObject(input, String.class), kryo.readObject(input, ParameterList.class), kryo.readObject(input, String.class));
            }
        });
        kryo.register(XComponent.class, new Serializer<XComponent>() {
            @Override
            public void write(Kryo kryo, Output output, XComponent object) {
                kryo.writeObject(output, object.getName());
                kryo.writeObject(output, object.getProperties());
            }

            @Override
            public XComponent read(Kryo kryo, Input input, Class<XComponent> type) {
                return new XComponent(kryo.readObject(input, String.class), kryo.readObject(input, PropertyList.class));
            }
        });
        kryo.register(VTimeZone.class, new Serializer<VTimeZone>() {
            @Override
            public void write(Kryo kryo, Output output, VTimeZone object) {
                kryo.writeObject(output, object.getProperties());
                kryo.writeObject(output, object.getObservances());
            }

            @Override
            public VTimeZone read(Kryo kryo, Input input, Class<VTimeZone> type) {
                //noinspection unchecked
                return new VTimeZone(kryo.readObject(input, PropertyList.class), kryo.readObject(input, ComponentList.class));
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
        kryo.register(DateTime.class);
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
                kryo.writeObjectOrNull(output, object.getTimeZone(), TimeZone.class);
            }

            @Override
            public DtStart read(Kryo kryo, Input input, Class<DtStart> type) {
                DtStart dtStart = new DtStart(kryo.readObject(input, ParameterList.class), kryo.readObject(input, Date.class));
                TimeZone timeZone = kryo.readObjectOrNull(input, TimeZone.class);
                if (timeZone != null && !dtStart.isUtc())
                    dtStart.setTimeZone(timeZone);
                return dtStart;
            }
        });
        kryo.register(DtEnd.class, new Serializer<DtEnd>() {
            @Override
            public void write(Kryo kryo, Output output, DtEnd object) {
                kryo.writeObject(output, object.getParameters());
                kryo.writeObject(output, object.getDate());
                kryo.writeObjectOrNull(output, object.getTimeZone(), TimeZone.class);
            }

            @Override
            public DtEnd read(Kryo kryo, Input input, Class<DtEnd> type) {
                DtEnd dtEnd = new DtEnd(kryo.readObject(input, ParameterList.class), kryo.readObject(input, Date.class));
                TimeZone timeZone = kryo.readObjectOrNull(input, TimeZone.class);
                if (timeZone != null && !dtEnd.isUtc())
                    dtEnd.setTimeZone(timeZone);
                return dtEnd;
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

        return kryo;
    }

    public static class KryoRegistrationFactory implements KryoFactory {

        @Override
        public Kryo create() {
            return registerKryoClasses(new Kryo());
        }
    }
}
