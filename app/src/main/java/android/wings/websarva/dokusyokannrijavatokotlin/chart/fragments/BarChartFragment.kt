package android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentBarChartBinding
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.core.content.ContextCompat
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


class BarChartFragment : Fragment() {

    private lateinit var realm: Realm
    private var _binding: FragmentBarChartBinding? = null
    private val binding get() = _binding!!
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        realm = RealmManager.getGraphRealmInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //現在の年月を取得
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        binding.currentYearTextView.text = year.toString()

        //realmからグラフの情報を取得する。
        val record = realm.where(GraphObject::class.java).findAll()
        val lastYearRecord = record.lastOrNull { it.year == year - 1 }

        // 昨年の読書数を表示
        if (lastYearRecord == null) {
            binding.lastYearReadBookCount.text = "0"
        } else {
            binding.lastYearReadBookCount.text = lastYearRecord.graphList.sum("readCount").toString()
            // 今月が1月であれば、昨年の12月の読書数を表示
            if (month == 0) {
                binding.lastMonthReadBookCount.text = lastYearRecord.graphList[11]?.readCount.toString()
            }
        }

        // 今年の読書数を表示する
        val thisYearRecord = record.lastOrNull { it.year == year }
        if (thisYearRecord == null) {
            binding.thisYearReadBookCount.text = "0"
            binding.thisMonthReadBookCount.text = "0"
            binding.lastMonthReadBookCount.text = "0"
        } else {
            binding.thisYearReadBookCount.text = thisYearRecord.graphList.sum("readCount").toString()
            binding.thisMonthReadBookCount.text = thisYearRecord.graphList[month]?.readCount.toString()
            if (month != 0) {
                binding.lastMonthReadBookCount.text =
                    thisYearRecord.graphList[month - 1]?.readCount.toString()
            }
        }

        setUpChart(binding.chart, thisYearRecord)

        // 1年戻る
        binding.backYearTextView.setOnClickListener {
            currentYear -= 1
            val yearRecord = record.lastOrNull { it.year == currentYear }
            binding.currentYearTextView.text = getString(R.string.graph_current_year, currentYear.toString())
            setUpChart(binding.chart, yearRecord)
        }

        // 1年進む
        binding.nextYearTextView.setOnClickListener {
            currentYear += 1
            val yearRecord = record.lastOrNull { it.year == currentYear }
            binding.currentYearTextView.text = getString(R.string.graph_current_year, currentYear.toString())
            setUpChart(binding.chart, yearRecord)
        }
    }

    /**
     * グラフの設定をするメソッド
     * @param chart 縦グラフ
     */
    private fun setUpChart(chart: BarChart, record: GraphObject?) {

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
        val monthLabel =
            arrayOf("", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月")

        //x軸に単位が表示される。
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(monthLabel)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)

        //x軸の表示するラベルの数を指定。今回の場合は、12月までを表示したいから12を値として入れる。
        chart.xAxis.labelCount = 12

        //グラフの意味を説明。今回はいらない。
        chart.legend.isEnabled = false

        // y軸のラベル設定
        chart.axisLeft.valueFormatter = MyYAxisValueFormatter()
        chart.axisLeft.spaceTop = 15F
        chart.axisLeft.setDrawGridLines(false)

        // データのリストを宣言
        val values = ArrayList<BarEntry>()

        // 各月のデータを取得
        for (i in 1..12) {
            val month = record?.graphList?.get(i - 1)
            if (month != null) {
                values.add(BarEntry(i.toFloat(), month.readCount.toFloat()))
            } else {
                values.add(BarEntry(i.toFloat(), 0F))
            }
        }

        // データをセット
        val set1 = BarDataSet(values, "Data Set")
        set1.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)

        //グラフ上に値を表示。
        set1.setDrawValues(true)
        set1.valueFormatter = InsideValueFormatter()
        set1.valueTextSize = 15F

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)

        chart.data = BarData(dataSets)
        chart.setFitBars(true)

        chart.invalidate()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            BarChartFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}