package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentInputActionBinding
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.main.navigator.MainNavigator
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import androidx.appcompat.app.AlertDialog
import java.util.*


class InputActionFragment : BaseDetailFragment(), TextWatcher {

    private lateinit var actionId: String
    private var saveMenu: Button? = null
    private var actionObj: ActionPlanObject? = null
    private var _binding: FragmentInputActionBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            actionId = it.getString(ActionListFragment.ACTION_ID, "")
        }

        // 行動の情報を取得
        if (actionId != "") {
            actionObj =
               bookObj?.actionPlanDairy?.last { it.id == actionId }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        binding.actionTitleInput.addTextChangedListener(this)
        binding.actionWhatInput.addTextChangedListener(this)
        binding.actionCouldInput.addTextChangedListener(this)
        binding.actionCouldNotInput.addTextChangedListener(this)
        binding.actionNextInput.addTextChangedListener(this)

        if (actionId != "") {
            setEditTexts()
        }
    }

    /**
     * EditTextにテキストをセットするメソッド
     */
    private fun setEditTexts() {
        binding.actionTitleInput.setText(actionObj?.title)
        binding.actionWhatInput.setText(actionObj?.what)
        binding.actionCouldInput.setText(actionObj?.could)
        binding.actionCouldNotInput.setText(actionObj?.couldNot)
        binding.actionNextInput.setText(actionObj?.nextAction)
    }

    /**
     * BookObjectのactionPlanDiaryをrealmに保存するメソッド
     */
    private fun saveData() {
        realm.executeTransaction {
            val obj = it.createObject(ActionPlanObject::class.java, UUID.randomUUID().toString())
            obj.title = binding.actionTitleInput.text.toString()
            obj.what = binding.actionWhatInput.text.toString()
            obj.could = binding.actionCouldInput.text.toString()
            obj.couldNot = binding.actionCouldNotInput.text.toString()
            obj.nextAction = binding.actionNextInput.text.toString()
            bookObj?.actionPlanDairy?.add(obj)
            bookObj?.actionPlan = binding.actionNextInput.text.toString()
        }
        MainNavigator.setActionFlag()
    }

    /**
     * actionPlanDiaryをrealmに保存するメソッド
     */
    private fun updateData() {
        realm.executeTransaction {
            actionObj?.title = binding.actionTitleInput.text.toString()
            actionObj?.what = binding.actionWhatInput.text.toString()
            actionObj?.could = binding.actionCouldInput.text.toString()
            actionObj?.couldNot = binding.actionCouldNotInput.text.toString()
            actionObj?.nextAction = binding.actionNextInput.text.toString()
        }
    }

    /**
     * EditTextにテキストが入力されているかチェックするメソッド
     * @return Boolean型を返す
     */
    private fun isInput(): Boolean {
        return !binding.actionTitleInput.text.isNullOrEmpty() && !binding.actionWhatInput.text.isNullOrEmpty() && !binding.actionCouldInput.text.isNullOrEmpty()
                && !binding.actionCouldNotInput.text.isNullOrEmpty() && !binding.actionNextInput.text.isNullOrEmpty()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_register_menu, menu)
        val saveItem = menu.findItem(R.id.actionSaveItem)
        saveMenu = saveItem.actionView.findViewById(R.id.saveButton)
        saveMenu?.isEnabled = isInput()
        saveMenu?.setOnClickListener {

            // データの登録または更新
            if (actionId == "") {
                saveData()
            } else {
                updateData()
            }

            // キーボードの非表示
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // 登録または更新に成功したらダイアログを表示する
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.dialog_register_message)
                .setPositiveButton(R.string.dialog_positive) { dialog, _ ->
                    dialog.dismiss()
                    requireActivity().supportFragmentManager.popBackStack()
                }.show()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        saveMenu?.isEnabled = isInput()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            InputActionFragment().apply {
                arguments = Bundle().apply {

                }
            }

        @JvmStatic
        fun newInstance(id: String) =
            InputActionFragment().apply {
                arguments = Bundle().apply {
                    putString(ActionListFragment.ACTION_ID, id)
                }
            }

    }
}

