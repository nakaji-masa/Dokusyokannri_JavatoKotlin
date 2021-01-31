package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import java.util.*


class InputActionDairyDetailFragment : Fragment(), TextWatcher {

    private lateinit var realm: Realm
    private lateinit var bookId: String
    private lateinit var actionId: String
    private lateinit var titleEditText: EditText
    private lateinit var whatEditText: EditText
    private lateinit var couldEditText: EditText
    private lateinit var couldNotEditText: EditText
    private lateinit var actionNextEditText: EditText
    private var menuSaveButton: Button? = null
    private var bookObj: BookObject? = null
    private var actionObj: ActionPlanObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(DetailActivity.BOOK_ID, "")
            actionId = it.getString(DetailActivity.ACTION_PLAN_ID, "")
        }
        realm = Realm.getInstance(RealmConfigObject.bookConfig)

        bookObj = realm.where(BookObject::class.java).equalTo("id", bookId).findAll().last()
        if (actionId != "") {
            actionObj =
               bookObj?.actionPlanDairy?.last { it.id == actionId }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input_action_dairy_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        titleEditText = view.findViewById(R.id.actionTitleInput)
        whatEditText = view.findViewById(R.id.actionWhatInput)
        couldEditText = view.findViewById(R.id.actionCouldInput)
        couldNotEditText = view.findViewById(R.id.actionCouldNotInput)
        actionNextEditText = view.findViewById(R.id.actionNextInput)

        titleEditText.addTextChangedListener(this)
        whatEditText.addTextChangedListener(this)
        couldEditText.addTextChangedListener(this)
        couldNotEditText.addTextChangedListener(this)
        actionNextEditText.addTextChangedListener(this)

        if (actionId != "") {
            setEditTexts()
        }
    }

    /**
     * EditTextにテキストをセットするメソッド
     */
    private fun setEditTexts() {
        titleEditText.setText(actionObj?.title)
        whatEditText.setText(actionObj?.what)
        couldEditText.setText(actionObj?.could)
        couldNotEditText.setText(actionObj?.couldNot)
        actionNextEditText.setText(actionObj?.nextAction)
    }

    /**
     * BookObjectのactionPlanDiaryをrealmに保存するメソッド
     */
    private fun saveData() {
        realm.executeTransaction {
            val obj = it.createObject(ActionPlanObject::class.java, UUID.randomUUID().toString())
            obj.title = titleEditText.text.toString()
            obj.what = whatEditText.text.toString()
            obj.could = couldEditText.text.toString()
            obj.couldNot = couldNotEditText.text.toString()
            obj.nextAction = actionNextEditText.text.toString()
            bookObj?.actionPlanDairy?.add(obj)
            bookObj?.actionPlan = actionNextEditText.text.toString()
            bookObj?.actionPlan = actionNextEditText.text.toString()
        }
    }

    /**
     * actionPlanDiaryをrealmに保存するメソッド
     */
    private fun updateData() {
        realm.executeTransaction {
            actionObj?.title = titleEditText.text.toString()
            actionObj?.what = whatEditText.text.toString()
            actionObj?.could = couldEditText.text.toString()
            actionObj?.couldNot = couldNotEditText.text.toString()
            actionObj?.nextAction = actionNextEditText.text.toString()
        }
    }

    /**
     * EditTextにテキストが入力されているかチェックするメソッド
     * @return Boolean型を返す
     */
    private fun isInput(): Boolean {
        return titleEditText.text.isNotEmpty() && whatEditText.text.isNotEmpty() && couldEditText.text.isNotEmpty() && couldNotEditText.text.isNotEmpty() && actionNextEditText.text.isNotEmpty()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_register_menu, menu)
        val saveItem = menu.findItem(R.id.actionSaveItem)
        println("onCreateOptionsMenu")
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
            AlertDialog.Builder(it.context, R.style.BasicDialogTheme)
                .setMessage(R.string.dialog_register_message)
                .setPositiveButton(R.string.dialog_positive) { dialog, which ->
                    dialog.dismiss()
                    activity?.supportFragmentManager?.popBackStack()
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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        @JvmStatic
        fun newInstance(bookId: String) =
            InputActionDairyDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, bookId)
                }
            }

        @JvmStatic
        fun newInstance(bookId: String, actionId: String) =
            InputActionDairyDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, bookId)
                    putString(DetailActivity.ACTION_PLAN_ID, actionId)
                }
            }

    }
}

