package android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentPieChartBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import io.realm.Realm
import io.realm.kotlin.where
import kotlin.collections.ArrayList


class PieChartFragment : Fragment() {

    private lateinit var realm: Realm
    private var bookRate: Float = 0F
    private var actionRate: Float = 0F
    private var _binding: FragmentPieChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        realm = RealmManager.getBookRealmInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPieChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupPieChart()
    }

    /**
     * PieChartをセットアップするメソッド
     */
    private fun setupPieChart() {
        // グラフの説明
        binding.pieChart.description.isEnabled = false

        // タッチしてもグラフが動かないようにする
        binding.pieChart.setTouchEnabled(false)

        // グラフのデータ
        setRateAndText()
        val values = listOf(bookRate, actionRate)
        val labels = listOf("読書", "行動")
        val entry = ArrayList<PieEntry>()
        for (i in values.indices) {
            entry.add(PieEntry(values[i], labels[i]))
        }

        val dataSet = PieDataSet(entry, "")
        dataSet.colors = listOf(Color.parseColor("#42a5f5"), Color.parseColor("#ef5350"))
        dataSet.setDrawValues(true)

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(20f)
        pieData.setValueTextColor(Color.WHITE)

        binding.pieChart.data = pieData
    }

    /**
     * フィールドの読んだ本とアクションプランをセット
     * テキストビューにセット
     */
    private fun setRateAndText() {
        val book = realm.where<BookObject>().equalTo("uid", AuthHelper.getUid()).findAll()

        // 読んだ本をセット
        val bookCount = book.count().toFloat()

        // 行動の回数
        var actionCount = 0F

        // 行動の回数をセット
        for (i in book.indices) {
            book[i]?.actionPlanDairy?.let {
                actionCount = actionCount.plus((it.count() - 1).toFloat())
            }
        }

        // テキストビューに値を表示
        binding.inputValue.text = bookCount.toInt().toString()
        binding.outputValue.text = actionCount.toInt().toString()

        val total = bookCount + actionCount

        // 割合をセット
        bookRate = ((bookCount / total) * 100)
        actionRate = ((actionCount / total) * 100)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class PercentFormatter: ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return String.format("%.1f", value) + "%"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            PieChartFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}