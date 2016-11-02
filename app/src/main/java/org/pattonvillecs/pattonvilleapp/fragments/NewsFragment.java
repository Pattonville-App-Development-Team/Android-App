package org.pattonvillecs.pattonvilleapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.PreferenceUtils;
import org.pattonvillecs.pattonvilleapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {

    private TextView textView;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */
    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_news);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        textView = (TextView) root.findViewById(R.id.newstest);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] mSelectedSchools = PreferenceUtils.getSelectedSchools(getContext());

        if (mSelectedSchools.length > 0) {
            String string = "";
            for (int i = 0; i < mSelectedSchools.length; i++) {
                string += mSelectedSchools[i] + ", ";
            }

            textView.setText(string);
        } else {
            textView.setText("No Schools Selected");
        }
    }
}
