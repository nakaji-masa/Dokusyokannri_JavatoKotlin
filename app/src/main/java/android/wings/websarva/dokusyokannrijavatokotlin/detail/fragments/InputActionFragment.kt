package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.main.navigator.MainNavigator
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_input_action.*
import java.util.*


class InputActionFragment : BaseDetailFragment(), TextWatcher {

    private lateinit var actionId: String
    private var menuSaveButton: Button? = null
    private var actionObj: ActionPlanObject? = null

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
    ): View? {
        return inflater.inflate(R.layout.fragment_input_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        actionTitleInput.addTextChangedListener(this)
        actionWhatInput.addTextChangedListener(this)
        actionCouldInput.addTextChangedListener(this)
        actionCouldNotInput.addTextChangedListener(this)
        actionNextInput.addTextChangedListener(this)

        if (actionId != "") {
            setEditTexts()
        }
    }

    /**
     * EditTextにテキストをセットするメソッド
     */
    private fun setEditTexts() {
        actionTitleInput.setText(actionObj?.title)
        actionWhatInput.setText(actionObj?.what)
        actionCouldInput.setText(actionObj?.could)
        actionCouldNotInput.setText(actionObj?.couldNot)
        actionNextInput.setText(actionObj?.nextAction)
    }

    /**
     * BookObjectのactionPlanDiaryをrealmに保存するメソッド
     */
    private fun saveData() {
        realm.executeTransaction {
            val obj = it.createObject(ActionPlanObject::class.java, UUID.randomUUID().toString())
            obj.title = actionTitleInput.text.toString()
            obj.what = actionWhatInput.text.toString()
            obj.could = actionCouldInput.text.toString()
            obj.couldNot = actionCouldNotInput.text.toString()
            obj.nextAction = actionNextInput.text.toString()
            bookObj?.actionPlanDairy?.add(obj)
            bookObj?.actionPlan = actionNextInput.text.toString()
        }
        MainNavigator.setActionFlag()
    }

    /**
     * actionPlanDiaryをrealmに保存するメソッド
     */
    private fun updateData() {
        realm.executeTransaction {
            actionObj?.title = actionTitleInput.text.toString()
            actionObj?.what = actionWhatInput.text.toString()
            actionObj?.could = actionCouldInput.text.toString()
            actionObj?.couldNot = actionCouldNotInput.text.toString()
            actionObj?.nextAction = actionNextInput.text.toString()
        }
    }

    /**
     * EditTextにテキストが入力されているかチェックするメソッド
     * @return Boolean型を返す
     */
    private fun isInput(): Boolean {
        return !actionTitleInput.text.isNullOrEmpty() && !actionWhatInput.text.isNullOrEmpty() && !actionCouldInput.text.isNullOrEmpty()
                && !actionCouldNotInput.text.isNullOrEmpty() && !actionNextInput.text.isNullOrEmpty()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_register_menu, menu)
        val saveItem = menu.findItem(R.id.actionSaveItem)
        menuSaveButton = saveItem.actionView.findViewById(R.id.saveButton)
        menuSaveButton?.isEnabled = isInput()
        menuSaveButton?.setOnClickListener {

            // データの登録または更新
            if (actionId == "") {
                saveData()
            } else {
                updateData()
            }

            // キーボードの非表示
            val inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // 登録または更新に成功したらダイアログを表示する
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.dialog_register_message)
                .setPositiveButton(R.string.dialog_positive) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        menuSaveButton?.isEnabled = isInput()
    }

    override fun afterTextChanged(s: Editable?) {
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

