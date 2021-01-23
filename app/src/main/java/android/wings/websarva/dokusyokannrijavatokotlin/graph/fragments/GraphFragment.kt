package android.wings.websarva.dokusyokannrijavatokotlin.graph.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList


class GraphFragment : Fragment() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        realm = Realm.getInstance(RealmConfigObject.graphConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graph, container, false)

        //現在の年月を取得
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        var bookThisYearCount = 0
        var bookLastYearCount = 0
        var bookThisMonthCount = 0
        var bookLastMonthCount = 0


        //realmからグラフの情報を取得する。
        val record = realm.where(GraphObject::class.java).findAll()
        val lastYearRecord = record.lastOrNull { it.year == year - 1 }
        lastYearRecord?.let {
            bookLastYearCount = it.graphList.sum("readCount").toInt()
        }
        val thisYearRecord = record.lastOrNull { it.year == year }
        thisYearRecord?.let { graphObj ->
            bookThisYearCount = graphObj.graphList.sum("readCount").toInt()
            graphObj.graphList[month]?.let {
                bookThisMonthCount = it.readCount
            }
            if (month == 0) {
                lastYearRecord?.graphList?.get(11)?.let {
                    bookLastMonthCount = it.readCount
                }
            } else {
                graphObj.graphList[month - 1]?.let {
                    bookLastMonthCount = it.readCount
                }
            }
        }


        //今月のTextViewに入れる
        val thisMonthTextView = view.findViewById<TextView>(R.id.thisMonthReadBookCount)
        thisMonthTextView.text = bookThisMonthCount.toString()

        //先月のTextViewに入れる
        val lastMonthTextView = view.findViewById<TextView>(R.id.lastMonthReadBookCount)
        lastMonthTextView.text = bookLastMonthCount.toString()

        //今年のTextViewに入れる
        val thisYearTextView = view.findViewById<TextView>(R.id.thisYearReadBookCount)
        thisYearTextView.text = bookThisYearCount.toString()

        //昨年のTextViewに入れる
        val lastYearTextView = view.findViewById<TextView>(R.id.lastYearReadBookCount)
        lastYearTextView.text = bookLastYearCount.toString()

        val chart = view.findViewById<BarChart>(R.id.chart)

        val values = ArrayList<BarEntry>()

        setBookCount(thisYearRecord, values)

        setUpChart(chart)

        val set1 = BarDataSet(values, "Data Set")
        set1.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)

        //グラフ上に値を表示。
        set1.setDrawValues(true)
        set1.valueFormatter = InsideValueFormatter()
        set1.valueTextSize = 15F


        val dataSets = ArrayList<IBarDataSet>()

        dataSets.add(set1)
        val data = BarData(dataSets)
        chart.data = data
        chart.setFitBars(true)

        chart.invalidate()
        return view
    }

    /**
     * グラフの設定をするメソッド
     */
    private fun setUpChart(chart: BarChart) {

        //グラフのタイトル設定
        chart.description.isEnabled = false


        //背景やズームを禁止する。
        chart.setPinchZoom(false)
        chart.setTouchEnabled(false)
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
        val month =
            arrayOf("", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月")
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
        chart.axisLeft.setDrawGridLines(false)

    }

    /**
     * 1月から12月までの値を格納するメソッド
     */
    private fun setBookCount(record: GraphObject?, values: ArrayList<BarEntry>) {
        for (i in 1..12) {
            val month = record?.graphList?.get(i - 1)
            if (month != null) {
                values.add(BarEntry(i.toFloat(), month.readCount.toFloat()))
            } else {
                values.add(BarEntry(i.toFloat(), 0F))
            }
        }
    }


    /**
     * y軸のラベルを設定するクラスです
     */
    class MyYAxisValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString() + "冊"
        }

    }

    /**
     * グラフ内のラベルを設定するメソッドです
     */
    class InsideValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GraphFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


}