package android.wings.websarva.dokusyokannrijavatokotlin

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import io.realm.Realm
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class GraphFragment : Fragment() {

    private lateinit var realm : Realm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graph, container, false)

        val chart = view.findViewById<BarChart>(R.id.chart)

        val values = ArrayList<BarEntry>()

        setBookCount(12, values)

        setUpChart(chart)


        val set1 = BarDataSet(values, "Data Set")
        set1.color = Color.parseColor("#99cc00")

        //グラフ上に値を表示。
        set1.setDrawValues(true)


        val dataSets = ArrayList<IBarDataSet>()

        dataSets.add(set1)
        val data = BarData(dataSets)
        chart.data = data
        chart.setFitBars(true)

        chart.invalidate()
        return view
    }

    private fun setUpChart(chart: BarChart) {

        //グラフの説明を消す
        chart.description.isEnabled = false

        //背景やズームを禁止する。
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.setMaxVisibleValueCount(20)
        chart.setDrawBarShadow(false)

        //右側の値は表示しない
        chart.axisRight.isEnabled = false

        //左側のy軸の設定
        chart.axisLeft.axisMaximum = 50F
        chart.axisLeft.axisMinimum = 0F
        chart.axisLeft.labelCount = 10

        //x軸の設定
        val month = arrayOf("", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月")
        //x軸に単位が表示される。
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(month)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)

        //x軸の表示するラベルの数を指定。今回の場合は、12月までを表示したいから12を値として入れる。
        chart.xAxis.labelCount = 12

        //グラフの意味を説明。今回はいらない。
        chart.legend.isEnabled = false

        chart.axisLeft.valueFormatter = MyYAxisValueFormatter()
        chart.axisLeft.spaceTop = 15F
        chart.axisLeft.setDrawTopYLabelEntry(true)

    }

    private fun setBookCount(count : Int, values : ArrayList<BarEntry>) {
        var readBook = 1F
        var month = 1F
        for(i in 1..count) {
            values.add(BarEntry(month, readBook))
            readBook += 3
            month += 1
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraphFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    //y軸に単位を入れてあげる。
    class MyYAxisValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString() + "冊"
        }

    }


}