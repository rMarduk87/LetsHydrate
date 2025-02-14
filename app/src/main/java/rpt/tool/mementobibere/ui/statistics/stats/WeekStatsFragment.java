package rpt.tool.mementobibere.ui.statistics.stats;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.waterdiary.drinkreminder.adapter.HistoryAdapter;
import com.waterdiary.drinkreminder.base.MasterBaseAppCompatActivity;
import com.waterdiary.drinkreminder.base.MasterBaseFragment;
import com.waterdiary.drinkreminder.model.History;
import com.waterdiary.drinkreminder.utils.URLFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class WeekStatsFragment extends MasterBaseFragment
{
	View item_view;

	ImageView img_pre,img_next;
	AppCompatTextView lbl_title;
	LineChart chartNew;

	List<String> lst_date=new ArrayList<>();
	List<Integer> lst_date_val=new ArrayList<>();
	List<Integer> lst_date_goal_val=new ArrayList<>();
	List<Integer> lst_date_goal_val_2=new ArrayList<>();
	ArrayList<String> lst_week=new ArrayList<>();

	BottomSheetDialog bottomSheetDialog;
	ArrayList<History> histories=new ArrayList<>();
	HistoryAdapter adapter;


	BarChart chart;

	Calendar current_start_calendar;
	Calendar current_end_calendar;

	Calendar start_calendarN;
	Calendar end_calendarN;


	AppCompatTextView txt_avg_intake,txt_drink_fre,txt_drink_com;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		item_view=inflater.inflate(R.layout.screen_week_report, container, false);

		lst_week.clear();
		lst_week.add(sh.get_string(R.string.sun));
		lst_week.add(sh.get_string(R.string.mon));
		lst_week.add(sh.get_string(R.string.tue));
		lst_week.add(sh.get_string(R.string.wed));
		lst_week.add(sh.get_string(R.string.thu));
		lst_week.add(sh.get_string(R.string.fri));
		lst_week.add(sh.get_string(R.string.sat));


		FindViewById();

		setCurrentWeekInfo();

		Body();

		return item_view;
	}

	private void FindViewById()
	{
		chartNew=item_view.findViewById(R.id.chartNew);
		chart = item_view.findViewById(R.id.chart1);

		lbl_title=item_view.findViewById(R.id.lbl_title);
		img_pre=item_view.findViewById(R.id.img_pre);
		img_next=item_view.findViewById(R.id.img_next);

		txt_avg_intake=item_view.findViewById(R.id.txt_avg_intake);
		txt_drink_fre=item_view.findViewById(R.id.txt_drink_fre);
		txt_drink_com=item_view.findViewById(R.id.txt_drink_com);
	}

	public void setCurrentWeekInfo()
	{
		current_start_calendar = Calendar.getInstance(Locale.US);
		current_start_calendar.set(Calendar.DAY_OF_WEEK, 1);
		current_start_calendar.set(Calendar.HOUR_OF_DAY, 0);
		current_start_calendar.set(Calendar.MINUTE, 0);
		current_start_calendar.set(Calendar.SECOND, 0);
		current_start_calendar.set(Calendar.MILLISECOND, 0);

		current_end_calendar = Calendar.getInstance(Locale.US);
		current_end_calendar.set(Calendar.DAY_OF_WEEK, 7);
		current_end_calendar.set(Calendar.HOUR_OF_DAY, 23);
		current_end_calendar.set(Calendar.MINUTE, 59);
		current_end_calendar.set(Calendar.SECOND, 59);
		current_end_calendar.set(Calendar.MILLISECOND, 999);

		Log.d("START===========END",
				"\ncurrent_start_calendar : "+current_start_calendar.getTimeInMillis()+"  \n current_start_calendar : "+current_end_calendar.getTimeInMillis());
	}

	private void Body()
	{
		start_calendarN = Calendar.getInstance(Locale.US);
		start_calendarN.set(Calendar.DAY_OF_WEEK, 1);
		start_calendarN.set(Calendar.HOUR_OF_DAY, 0);
		start_calendarN.set(Calendar.MINUTE, 0);
		start_calendarN.set(Calendar.SECOND, 0);
		start_calendarN.set(Calendar.MILLISECOND, 0);

		end_calendarN = Calendar.getInstance(Locale.US);
		end_calendarN.set(Calendar.DAY_OF_WEEK, 7);
		end_calendarN.set(Calendar.HOUR_OF_DAY, 23);
		end_calendarN.set(Calendar.MINUTE, 59);
		end_calendarN.set(Calendar.SECOND, 59);
		end_calendarN.set(Calendar.MILLISECOND, 999);


		loadData(start_calendarN,end_calendarN);

		img_pre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				start_calendarN.add(Calendar.DATE,-7);

				end_calendarN.add(Calendar.DATE,-7);

				/*Log.d("MIN_MAX_DATE 2.1 : ",""+
						start_calendarN.getTimeInMillis()+
						" @@@ "+""+end_calendarN.getTimeInMillis());*/


				loadData(start_calendarN,end_calendarN);

				generateBarDataNew();
			}
		});

		img_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				start_calendarN.add(Calendar.DATE,7);

				end_calendarN.add(Calendar.DATE,7);

				Log.d("MIN_MAX_DATE 2.1 : ",""+
						start_calendarN.getTimeInMillis()+
						" @@@ "+""+end_calendarN.getTimeInMillis());


				if(start_calendarN.getTimeInMillis()>=current_end_calendar.getTimeInMillis())
				{
					start_calendarN.add(Calendar.DATE,-7);
					end_calendarN.add(Calendar.DATE,-7);
					return;
				}

				loadData(start_calendarN,end_calendarN);

				generateBarDataNew();
			}
		});

		generateDataNew();

		generateBarDataNew();
	}



	public void loadData(Calendar start_calendar2,Calendar end_calendar2) {
		//lbl_title.setText(dth.getCurrentDate("MMM yyyy"));

		Calendar start_calendar = Calendar.getInstance(Locale.US);
		start_calendar.setTimeInMillis(start_calendar2.getTimeInMillis());
		Calendar end_calendar = Calendar.getInstance(Locale.US);
		end_calendar.setTimeInMillis(end_calendar2.getTimeInMillis());

		lbl_title.setText(dth.getDate(start_calendar.getTimeInMillis(), "dd MMM")+" - "
				+dth.getDate(end_calendar.getTimeInMillis(), "dd MMM"));

		lst_date.clear();
		lst_date_goal_val.clear();
		lst_date_val.clear();
		lst_date_goal_val_2.clear();


		do {
			Log.d("DATE2 : ",""+sh.get_2_point(""+start_calendar.get(Calendar.DAY_OF_MONTH))+"-"+sh.get_2_point(""+(start_calendar.get(Calendar.MONTH)+1))+"-"+start_calendar.get(Calendar.YEAR));

			lst_date.add(""+sh.get_2_point(""+start_calendar.get(Calendar.DAY_OF_MONTH))+"-"+sh.get_2_point(""+(start_calendar.get(Calendar.MONTH)+1))+"-"+start_calendar.get(Calendar.YEAR));
			//lst_date.add(dth.getDate(start_calendar.getTimeInMillis(),"dd"));

			start_calendar.add(Calendar.DATE,1);
		}
		while (start_calendar.getTimeInMillis()<=end_calendar.getTimeInMillis());

		int day_counter=0;
		double total_drink=0;
		int frequency_counter=0;
		double total_goal=0;

		for (int i = 0; i < lst_date.size(); i++)
		{
			ArrayList<HashMap<String, String>> arr_data=dh.getdata("tbl_drink_details","DrinkDate ='"+lst_date.get(i)+"'");
			float tot=0;
			String last_goal="0";
			for(int j=0;j<arr_data.size();j++)
			{
				if(URLFactory.WATER_UNIT_VALUE.equalsIgnoreCase("ml")) {
					tot += Double.parseDouble(arr_data.get(j).get("ContainerValue"));
					last_goal=arr_data.get(j).get("TodayGoal");

					if(Double.parseDouble(arr_data.get(j).get("ContainerValue"))>0)
						frequency_counter++;
				}
				else {
					tot += Double.parseDouble(arr_data.get(j).get("ContainerValueOZ"));
					last_goal=arr_data.get(j).get("TodayGoalOZ");

					if(Double.parseDouble(arr_data.get(j).get("ContainerValueOZ"))>0)
						frequency_counter++;
				}
			}
			lst_date_val.add((int) tot);

			if(tot>0) {
				day_counter++;

				total_goal+=Float.parseFloat(last_goal);
			}

			total_drink+=tot;



			if(tot==0 && Math.round(Float.parseFloat(last_goal))==0)
			{
				int ii= (int) ph.getFloat(URLFactory.DAILY_WATER);

				lst_date_goal_val.add(ii);
				lst_date_goal_val_2.add(ii);
			}
			else if(tot>Math.round(Float.parseFloat(last_goal))) {
				lst_date_goal_val.add(0);
				lst_date_goal_val_2.add(Math.round(Float.parseFloat(last_goal)));
			}
			else {
				lst_date_goal_val.add(Math.round(Float.parseFloat(last_goal)) - (int) tot);
				lst_date_goal_val_2.add(Math.round(Float.parseFloat(last_goal)));
			}
		}


		//ah.Show_Alert_Dialog(total_drink+"   @@@@  "+total_goal);

		try {

			int avg = (int) Math.round(total_drink / day_counter);
			float f=Float.parseFloat(""+frequency_counter)/Float.parseFloat(""+day_counter);
			int avg_fre = Math.round(f);


			String str=avg_fre>1?sh.get_string(R.string.times):sh.get_string(R.string.time);

			txt_avg_intake.setText("" + avg + " " + URLFactory.WATER_UNIT_VALUE + "/"+sh.get_string(R.string.day));
			txt_drink_fre.setText("" + avg_fre +" " + str + "/"+sh.get_string(R.string.day));

		}
		catch (Exception e){
			txt_avg_intake.setText("0 " + URLFactory.WATER_UNIT_VALUE + "/"+sh.get_string(R.string.day));
			txt_drink_fre.setText("0 "+sh.get_string(R.string.time)+"/"+sh.get_string(R.string.day));
		}

		try {

			int avg_com = (int) Math.round((total_drink * 100) / total_goal);

			txt_drink_com.setText("" + avg_com + "%");
		}
		catch (Exception e){
			txt_drink_com.setText("0%");
		}


		Log.d("lst_date_val : ",""+new Gson().toJson(lst_date_val));
		Log.d("lst_date_val 2 : ",""+new Gson().toJson(lst_date_goal_val));
	}

	private final RectF onValueSelectedRectF = new RectF();

	public void generateBarDataNew()
	{
		chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, Highlight h)
			{
				if (e == null)
					return;

				try {



					if (lst_date_val.get((int) e.getX()) > 0)
						showDayDetailsDialog((int) e.getX());


				}
				catch (Exception ex){}


				/*if (e == null)
					return;

				RectF bounds = onValueSelectedRectF;
				chart.getBarBounds((BarEntry) e, bounds);
				MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

				Log.i("bounds", bounds.toString());
				Log.i("position", position.toString());

				Log.i("x-index", "low: " + chart.getLowestVisibleX() + ", high: "
						+ chart.getHighestVisibleX());

				MPPointF.recycleInstance(position);*/

				//BarEntry entry = (BarEntry) e;

				//ah.customAlert("call");

				/*if (entry.getYVals() != null) {
					Log.i("VAL SELECTED", "Value: " + entry.getYVals()[h.getStackIndex()]);
					ah.customAlert("call : "+entry.getYVals()[h.getStackIndex()]);
				}
				else {
					Log.i("VAL SELECTED", "Value: " + entry.getY());
					ah.customAlert("call : "+entry.getY());
				}*/
			}

			@Override
			public void onNothingSelected() {

			}
		});

		chart.clear();



		chart.getDescription().setEnabled(false);

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		chart.setMaxVisibleValueCount(40);

		chart.setDrawGridBackground(false);
		chart.setDrawBarShadow(false);

		chart.setDrawValueAboveBar(false);
		chart.setHighlightFullBarEnabled(false);

		//chart.setHighlightPerTapEnabled(false);

		// change the position of the y-labels
		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setTextColor(mContext.getResources().getColor(R.color.rdo_gender_select));

		//leftAxis.setDrawGridLines(false);
		//leftAxis.setGranularityEnabled(false);

		//chart.setViewPortOffsets(0f,0f,0f,0f);

		leftAxis.setAxisMaximum(getMaxBarGraphVal());

		leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
		chart.getAxisRight().setEnabled(false);

		chart.setExtraBottomOffset(20);

		XAxis xLabels = chart.getXAxis();
		//xLabels.setDrawAxisLine(false);
		xLabels.setDrawGridLines(false);
		xLabels.setGranularityEnabled(false);
		xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
		xLabels.setTextColor(mContext.getResources().getColor(R.color.rdo_gender_select));

		ValueFormatter xAxisFormatter =new ValueFormatter() {
			@Override
			public String getFormattedValue(float value)
			{
				try {
					if (lst_week.size() > (int) value)
						return lst_week.get((int) value);
				}
				catch (Exception e){}
				return "N/A";
			}
		};

		xLabels.setValueFormatter(xAxisFormatter);

		// chart.setDrawXLabels(false);
		// chart.setDrawYLabels(false);



		Legend l = chart.getLegend();
		l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
		l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
		l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
		l.setDrawInside(false);
		l.setFormSize(8f);
		l.setFormToTextSpace(4f);
		l.setXEntrySpace(6f);
		l.setEnabled(false);

		ArrayList<BarEntry> values = new ArrayList<>();

		/*for (int i = 0; i < 31; i++) {
			float mul = (31 + 1);
			float val1 = (float) (Math.random() * mul) + mul / 3;
			float val2 = (float) (Math.random() * mul) + mul / 3;
			//float val3 = (float) (Math.random() * mul) + mul / 3;

			values.add(new BarEntry(
					i,
					new float[]{val1, val2},
					getResources().getDrawable(R.drawable.ic_launcher_background)));
		}*/

		for (int i = 0; i < lst_date.size(); i++) {
			float val1 = (float) lst_date_val.get(i);
			float val2 = (float) lst_date_goal_val.get(i);

			Log.d("========",""+val1+" @@@ "+val2);

			values.add(new BarEntry(
					i,
					val1,
					getResources().getDrawable(R.drawable.ic_launcher_background)));
		}

		BarDataSet set1;

		if (chart.getData() != null &&
				chart.getData().getDataSetCount() > 0) {
			set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
			set1.setValues(values);
			set1.setHighLightAlpha(50);
			//set1.setValueTextColor(mContext.getResources().getColor(R.color.rdo_gender_select));
			set1.setDrawValues(false);
			chart.getData().notifyDataChanged();
			chart.notifyDataSetChanged();
		} else {
			set1 = new BarDataSet(values, "");
			set1.setDrawIcons(false);
			set1.setColors(getColors());
			set1.setHighLightAlpha(50);
			//set1.setStackLabels(new String[]{"Births", "Divorces", "Marriages"});

			ArrayList<IBarDataSet> dataSets = new ArrayList<>();
			dataSets.add(set1);

			BarData data = new BarData(dataSets);

			data.setValueFormatter(new ValueFormatter() {
				@Override
				public String getFormattedValue(float value)
				{
					try{
						if(value==0)
							return "";
						else
						{
							for(int k=0;k<lst_date_goal_val.size();k++)
							{
								if(lst_date_goal_val.get(k)==(int)value)
								{
									return ""+lst_date_goal_val_2.get(k);
								}
							}
						}
					}
					catch (Exception e){}

					return ""+(int)value;
				}
			});


			//data.setValueFormatter(new StackedValueFormatter(false, "", 1));

			//data.setValueTextColor(Color.WHITE);
			data.setValueTextColor(mContext.getResources().getColor(R.color.btn_back));
			set1.setDrawValues(false);
			set1.setValueTextSize(10);
			chart.setData(data);
		}

		chart.animateY(1500);

		chart.setPinchZoom(false);
		//chart.setFitBars(true);
		//chart.setDrawBarShadow(true);
		chart.setScaleEnabled(false);

		chart.invalidate();
	}

	public float getMaxBarGraphVal()
	{
		float drink_val=0;

		for(int k=0;k<lst_date_val.size();k++)
		{
			if(k==0)
			{
				drink_val = Float.parseFloat("" + lst_date_val.get(k));
				continue;
			}

			if(drink_val<Float.parseFloat("" + lst_date_val.get(k)))
				drink_val=Float.parseFloat("" + lst_date_val.get(k));

		}

		float goal_val=0;

		for(int k=0;k<lst_date_goal_val_2.size();k++)
		{
			if(k==0)
			{
				goal_val = Float.parseFloat("" + lst_date_goal_val_2.get(k));
				continue;
			}

			if(goal_val<Float.parseFloat("" + lst_date_goal_val_2.get(k)))
				goal_val=Float.parseFloat("" + lst_date_goal_val_2.get(k));

		}

		int singleUnit=URLFactory.WATER_UNIT_VALUE.equalsIgnoreCase("ml")?1000:35;

		float max_val=drink_val<goal_val?goal_val:drink_val;

		if(drink_val<1)
			max_val=singleUnit*3;

		max_val=(((int)(max_val/singleUnit))+1)*singleUnit;

		return max_val;
	}


	private int[] getColors() {

		// have as many colors as stack-values per entry
		int[] colors = new int[1];

		//System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 2);
		colors[0]=mContext.getResources().getColor(R.color.rdo_gender_select);
		//colors[1]=mContext.getResources().getColor(R.color.colorPrimary);

		return colors;
	}


	public void showDayDetailsDialog(final int position)
	{
		final Dialog dialog = new Dialog(act);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawable_background_tra);
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


		final View view = LayoutInflater.from(act).inflate(R.layout.dialog_day_info_chart, null, false);


		AppCompatTextView txt_date=view.findViewById(R.id.txt_date);
		AppCompatTextView txt_goal=view.findViewById(R.id.txt_goal);
		AppCompatTextView txt_consumed=view.findViewById(R.id.txt_consumed);
		AppCompatTextView txt_frequency=view.findViewById(R.id.txt_frequency);
		ImageView img_cancel=view.findViewById(R.id.img_cancel);


		//txt_date.setText(""+lst_date.get(position));
		txt_date.setText(dth.FormateDateFromString("dd-MM-yyyy","dd MMM",lst_date.get(position)));
		txt_goal.setText(""+lst_date_goal_val_2.get(position)+" "+URLFactory.WATER_UNIT_VALUE);
		txt_consumed.setText(""+lst_date_val.get(position)+" "+URLFactory.WATER_UNIT_VALUE);

		ArrayList<HashMap<String, String>> arr_data=dh.getdata("tbl_drink_details","DrinkDate ='"+lst_date.get(position)+"'");
		String str=arr_data.size()>1?sh.get_string(R.string.times):sh.get_string(R.string.time);
		txt_frequency.setText(arr_data.size()+" "+str);

		img_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();


			}
		});

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				chart.highlightValue(position,-1);
			}
		});

		dialog.setContentView(view);

		dialog.show();
	}




	private void generateDataNew()
	{
		{
			// background color
			chartNew.setBackgroundColor(Color.WHITE);

			chartNew.clear();

			// disable description text
			chartNew.getDescription().setEnabled(false);

			// enable touch gestures
			chartNew.setTouchEnabled(true);

			// set listeners
			chartNew.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
				@Override
				public void onValueSelected(Entry e, Highlight h)
				{
                    /*Log.i("Entry selected", e.toString());
                    Log.i("LOW HIGH", "low: " + chartNew.getLowestVisibleX() + ", high: " + chartNew.getHighestVisibleX());
                    Log.i("MIN MAX", "xMin: " + chartNew.getXChartMin() + ", xMax: " + chartNew.getXChartMax() + ", yMin: " + chartNew.getYChartMin() + ", yMax: " + chartNew.getYChartMax());
					Log.i("Entry selected", lst_date.get((int) e.getX()));*/

                    if(e.getY()>0) {
						//openMenuPicker("" + lst_date.get((int) e.getX()));
					}
				}

				@Override
				public void onNothingSelected() {
					//Log.i("Nothing selected", "Nothing selected.");
				}
			});
			chartNew.setDrawGridBackground(false);



			// enable scaling and dragging
			chartNew.setDragEnabled(true);
			chartNew.setScaleEnabled(true);
			// chart.setScaleXEnabled(true);
			// chart.setScaleYEnabled(true);

			// force pinch zoom along both axis
			chartNew.setPinchZoom(true);
		}

		XAxis xAxis;
		{   // // X-Axis Style // //
			xAxis = chartNew.getXAxis();

			xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

			xAxis.setLabelRotationAngle(-90);

			xAxis.setLabelCount(lst_date.size(),true);

			xAxis.setValueFormatter(new ValueFormatter() {
				@Override
				public String getFormattedValue(float value)
				{
					try{
						if(lst_date.size()>(int)value)
							return lst_date.get((int)value);
					}
					catch (Exception e){}
					return "N/A";
				}
			});

			// vertical grid lines
			xAxis.enableGridDashedLine(10f, 0f, 0f);
		}

		YAxis yAxis;
		{   // // Y-Axis Style // //
			yAxis = chartNew.getAxisLeft();

			// disable dual axis (only use LEFT axis)
			chartNew.getAxisRight().setEnabled(false);

			// horizontal grid lines
			yAxis.enableGridDashedLine(10f, 0f, 0f);

			// axis range
			yAxis.setAxisMaximum(getMaxGraphVal());
			yAxis.setAxisMinimum(-50f);
		}



		// add data
		setData(lst_date.size());

		// draw points over time
		//chart.animateX(1500);
		chartNew.animateY(1500);
		//chart.animateXY(2000, 2000);

		// get the legend (only possible after setting data)
		Legend l = chartNew.getLegend();

		// draw legend entries as lines
		l.setForm(Legend.LegendForm.LINE);
		//l.setEnabled(false);

		chartNew.setHorizontalScrollBarEnabled(true);
	}

	public float getMaxGraphVal()
	{
		//return 120;
        float val=1;

        for(int k=0;k<lst_date_val.size();k++)
        {
            if(k==0)
            {
                val = Float.parseFloat("" + lst_date_val.get(k));
                continue;
            }

            if(val<Float.parseFloat("" + lst_date_val.get(k)))
                val=Float.parseFloat("" + lst_date_val.get(k));

        }

        return val+100;
	}

	private void setData(int count) {

		ArrayList<Entry> values = new ArrayList<>();

		for (int i = 0; i < count; i++) {

			Random r = new Random();
			int i1 = r.nextInt(100 - 10) + 10;

			i1=lst_date_val.get(i);

			float val= Float.parseFloat(""+i1);
			values.add(new Entry(i, val, getResources().getDrawable(R.drawable.ic_add)));
		}

		LineDataSet set1;

		if (chartNew.getData() != null &&
				chartNew.getData().getDataSetCount() > 0) {
			set1 = (LineDataSet) chartNew.getData().getDataSetByIndex(0);
			set1.setValues(values);
			set1.notifyDataSetChanged();
			set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
			chartNew.getData().notifyDataChanged();
			chartNew.notifyDataSetChanged();
		} else {
			// create a dataset and give it a type
			set1 = new LineDataSet(values, "");

			set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

			set1.setDrawIcons(false);

			// draw dashed line
			set1.enableDashedLine(10f, 0f, 0f);

			// black lines and points
			set1.setColor(MasterBaseAppCompatActivity.getThemeColor(mContext));
			set1.setCircleColor(MasterBaseAppCompatActivity.getThemeColor(mContext));

			// line thickness and point size
			set1.setLineWidth(2f);
			set1.setCircleRadius(5f);

			// draw points as solid circles
			set1.setDrawCircleHole(false);

			// customize legend entry
			set1.setFormLineWidth(0f);
			set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 0f}, 0f));
			set1.setFormSize(15.f);

			// text size of values
			set1.setValueTextSize(9f);

			// draw selection line as dashed
			set1.enableDashedHighlightLine(10f, 5f, 0f);

			// set the filled area
			set1.setDrawFilled(true);
			set1.setFillFormatter(new IFillFormatter() {
				@Override
				public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
					return chartNew.getAxisLeft().getAxisMinimum();
				}
			});

			// set color of filled area
			if (Utils.getSDKInt() >= 18) {
				// drawables only supported on api level 18 and above
				Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.line_chart_fade_back);

				//create a new gradient color
				GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, MasterBaseAppCompatActivity.getThemeColorArray(act));

				set1.setFillDrawable(gd);
			} else {
				set1.setFillColor(Color.BLACK);
			}

			ArrayList<ILineDataSet> dataSets = new ArrayList<>();
			dataSets.add(set1); // add the data sets

			// create a data object with the data sets
			LineData data = new LineData(dataSets);

			// set data
			chartNew.setData(data);
		}
	}

	public void openMenuPicker(String selected_date)
	{
		bottomSheetDialog=new BottomSheetDialog(act);

		LayoutInflater layoutInflater=LayoutInflater.from(act);
		View view = layoutInflater.inflate(R.layout.screen_history, null, false);

		RecyclerView historyRecyclerView=view.findViewById(R.id.historyRecyclerView);

		load_history(selected_date);

		adapter = new HistoryAdapter(act, histories, new HistoryAdapter.CallBack() {
			@Override
			public void onClickSelect(History history, int position) {

			}

			@Override
			public void onClickRemove(History history, int position) {

			}
		});

		historyRecyclerView.setLayoutManager(new LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false));

		historyRecyclerView.setAdapter(adapter);

		bottomSheetDialog.setContentView(view);

		bottomSheetDialog.show();
	}

	public void load_history(String selected_date)
	{
		histories.clear();

		ArrayList<HashMap<String, String>> arr_data=dh.getdata("tbl_drink_details","DrinkDate ='"+selected_date+"'","datetime(substr(DrinkDateTime, 7, 4) || '-' || substr(DrinkDateTime, 4, 2) || '-' || substr(DrinkDateTime, 1, 2) || ' ' || substr(DrinkDateTime, 12, 8))",1);

        String mes_unit=URLFactory.WATER_UNIT_VALUE;

		for(int k=0;k<arr_data.size();k++)
		{
			History history=new History();
			history.setId(arr_data.get(k).get("id"));
            history.setContainerMeasure(mes_unit);
			history.setContainerValue(URLFactory.decimalFormat.format(Double.parseDouble(arr_data.get(k).get("ContainerValue")))+" "+mes_unit);
			history.setContainerValueOZ(URLFactory.decimalFormat.format(Double.parseDouble(arr_data.get(k).get("ContainerValueOZ")))+" "+mes_unit);
			history.setDrinkDate(arr_data.get(k).get("DrinkDate"));
			history.setDrinkTime(dth.FormateDateFromString("HH:mm","hh:mm a",arr_data.get(k).get("DrinkTime")));

			ArrayList<HashMap<String, String>> arr_data2=dh.getdata("tbl_drink_details","DrinkDate ='"+arr_data.get(k).get("DrinkDate")+"'");

			float tot=0;

			for(int j=0;j<arr_data2.size();j++)
			{
				if(mes_unit.equalsIgnoreCase("ml"))
					tot+=Double.parseDouble(arr_data2.get(j).get("ContainerValue"));
				else
					tot+=Double.parseDouble(arr_data2.get(j).get("ContainerValueOZ"));
			}

			history.setTotalML(URLFactory.decimalFormat.format(tot)+" "+mes_unit);

			histories.add(history);
		}
	}
}