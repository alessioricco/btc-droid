package it.alessioricco.btc.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import it.alessioricco.btc.R;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.HistoricalValueSample;
import it.alessioricco.btc.models.MarketHistory;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Chart.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Chart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chart extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_HISTORY = "history";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private MarketHistory mHistory;
    //private String mParam2;

    //@InjectView(R.id.chart)
    lecho.lib.hellocharts.view.LineChartView chart;


    private OnFragmentInteractionListener mListener;

    public Chart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param history Parameter 1.
     * @return A new instance of fragment Chart.
     */
    // TODO: Rename and change types and number of parameters
    public static Chart newInstance(MarketHistory history, String param2) {
        Chart fragment = new Chart();
        Bundle args = new Bundle();
        args.putSerializable(ARG_HISTORY, history);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHistory = (MarketHistory) getArguments().getSerializable(ARG_HISTORY);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            drawChart(mHistory);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chart, container, false);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    final public void update(MarketHistory marketHistory) {
        drawChart(marketHistory);
    }

    private void drawChart(MarketHistory marketHistory) {

        chart = ButterKnife.findById(this.getActivity(), R.id.chart);

        if (chart == null) {
            return;
        }

        if (marketHistory == null) {
            return;
        }

        final HistoricalValueSample history = marketHistory.getHistorySamples();

        if (history == null) {
            return;
        }

        final List<PointValue> values = new ArrayList<PointValue>();

        final List<AxisValue> axisValues = new ArrayList<AxisValue>();

        for(int i = 0; i< HistorySamplingHelper.MAX_SAMPLES ; i++ ){
            final HistoricalValue historicalValue = history.get(i);
            if (historicalValue != null) {
                final Double d = historicalValue.getValue();
                if (d != null) {
                    float value = historicalValue.getValue().floatValue();
                    values.add(new PointValue(i, value));
                    final AxisValue axisValue = new AxisValue(i);
                    axisValue.setLabel(HistorySamplingHelper.getSampleDescriptor(i).getLabel());
                    axisValues.add(axisValue);
                }
            }
        }

        final Line line = new Line(values)
                .setColor(Color.YELLOW)
                .setCubic(true);
        final List<Line> lines = new ArrayList<Line>();
        lines.add(line);
        final LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);

        final Axis axisY = new Axis().setHasLines(true);
        final Axis axisX = new Axis().setHasLines(false).setValues(axisValues);

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);

        chart.setLineChartData(lineChartData);
        chart.setVisibility(View.VISIBLE);

    }
}
