package android.wings.websarva.dokusyokannrijavatokotlin.graph.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphYearObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
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
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.*
import kotlin.collections.ArrayList


class GraphFragment : Fragment() {

    private lateinit var realm: Realm
    private var bookThisYearCount: Float? = 0F
    private var bookLastYearCount: Float? = 0F
    private var bookThisMonthCount: Float? = 0F
    private var bookLastMonthCount: Float? = 0F
    private var monthCount: ArrayList<Float> = ArrayList()

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

        //現在の年、月を取得
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        var index = 0


        //realmからグラフの情報を取得する。
        realm.executeTransaction {
            var graph = realm.where<GraphObject>().findFirst()
            if (graph == null) {
                graph = realm.createObject()
                graph.graphList.add(GraphYearObject())
                graph.graphList[index]?.year = calendar.get(Calendar.YEAR)
            }

            var graphYear = graph.graphList[index]


            if (graphYear?.year != year) {
                //年を決める
                for (i in 0..graph.graphList.size - 1) {
                    if (year == graph.graphList[i]?.year) {
                        graphYear = graph.graphList[i]
                        break
                    }

                    if (i == graph.graphList.size - 1) {
                        graph.graphList.add(GraphYearObject())
                        graphYear = graph.graphList[i + 1]
                        graphYear?.year = calendar.get(Calendar.YEAR)
                    }

                    index++
                }
            }

            if (month != 1) {
                getBookCount(graphYear, month)
            } else {
                bookThisMonthCount = graphYear?.jan
                if (index != 0) {
                    bookLastMonthCount = graph.graphList[index - 1]?.dec
                }
            }


            if (index != 0) {
                //今年の分の読んだ数を格納
                graph.graphList[index]?.jan?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.feb?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.mar?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.apr?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.may?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.jun?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.jul?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.aug?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.sep?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.oct?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.nov?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.dec?.let { it1 -> monthCount.add(it1) }

                bookLastYearCount =
                    graph.graphList[index - 1]?.jan?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.feb?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.mar?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.apr?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.may?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.jun?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.jul?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.aug?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.sep?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.oct?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.nov?.let { it1 -> bookLastYearCount?.plus(it1) }
                bookLastYearCount =
                    graph.graphList[index - 1]?.dec?.let { it1 -> bookLastYearCount?.plus(it1) }


            } else {
                graph.graphList[index]?.jan?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.feb?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.mar?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.apr?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.may?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.jun?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.jul?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.aug?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.sep?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.oct?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.nov?.let { it1 -> monthCount.add(it1) }
                graph.graphList[index]?.dec?.let { it1 -> monthCount.add(it1) }

            }

            //今年の本数を取得
            for (j in 0..monthCount.size - 1) {
                bookThisYearCount = bookThisYearCount?.plus(monthCount[j])
            }

        }


        //今月のTextViewに入れる
        val thisMonthTextView = view.findViewById<TextView>(R.id.thisMonthReadBookCount)
        thisMonthTextView.text = bookThisMonthCount?.toInt().toString()

        //先月のTextViewに入れる
        val lastMonthTextView = view.findViewById<TextView>(R.id.lastMonthReadBookCount)
        lastMonthTextView.text = bookLastMonthCount?.toInt().toString()

        //今年のTextViewに入れる
        val thisYearTextView = view.findViewById<TextView>(R.id.thisYearReadBookCount)
        thisYearTextView.text = bookThisYearCount?.toInt().toString()

        //昨年のTextViewに入れる
        val lastYearTextView = view.findViewById<TextView>(R.id.lastYearReadBookCount)
        lastYearTextView.text = bookLastYearCount?.toInt().toString()

        val chart = view.findViewById<BarChart>(R.id.chart)

        val values = ArrayList<BarEntry>()

        setBookCount(monthCount, values)

        setUpChart(chart)


        val set1 = BarDataSet(values, "Data Set")
        set1.color = resources.getColor(R.color.colorAccent)

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

    private fun setBookCount(monthArray: ArrayList<Float>, values: ArrayList<BarEntry>) {
        var element = 1F
        for (i in 1..monthArray.size) {
            values.add(BarEntry(element, monthArray[i - 1]))
            element++
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

    //中のラベル表示をする。
    class InsideValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    private fun getBookCount(graphYear: GraphYearObject?, month: Int) {

        //月を決める
        when (month) {

            2 -> {
                this.bookThisMonthCount = graphYear?.feb
                this.bookLastMonthCount = graphYear?.jan
            }

            3 -> {
                this.bookThisMonthCount = graphYear?.mar
                this.bookLastMonthCount = graphYear?.feb
            }

            4 -> {
                this.bookThisMonthCount = graphYear?.apr
                this.bookLastMonthCount = graphYear?.mar
            }

            5 -> {
                this.bookThisMonthCount = graphYear?.may
                this.bookLastMonthCount = graphYear?.apr
            }

            6 -> {
                this.bookThisMonthCount = graphYear?.jun
                this.bookLastMonthCount = graphYear?.may
            }

            7 -> {
                this.bookThisMonthCount = graphYear?.jul
                this.bookLastMonthCount = graphYear?.jun
            }

            8 -> {
                this.bookThisMonthCount = graphYear?.aug
                this.bookLastMonthCount = graphYear?.jul
            }

            9 -> {
                this.bookThisMonthCount = graphYear?.sep
                this.bookLastMonthCount = graphYear?.aug
            }

            10 -> {
                this.bookThisMonthCount = graphYear?.oct
                this.bookLastMonthCount = graphYear?.sep
            }

            11 -> {
                this.bookThisMonthCount = graphYear?.nov
                this.bookLastMonthCount = graphYear?.oct
            }

            12 -> {
                this.bookThisMonthCount = graphYear?.dec
                this.bookLastMonthCount = graphYear?.nov
            }
        }

    }


}