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
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import java.util.*


class InputActionDairyDetailFragment : Fragment(), TextWatcher {

    private lateinit var realm: Realm
    private lateinit var id: String
    private lateinit var menuSaveButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var whatEditText: EditText
    private lateinit var couldEditText: EditText
    private lateinit var couldNotEditText: EditText
    private lateinit var actionNextEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(DetailActivity.BOOK_ID, "")
        }
        realm = Realm.getInstance(RealmConfigObject.bookListConfig)
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
    }


    /**
     * BookObjectのactionPlanDiaryをrealmに保存する
     */
    private fun saveData() {
        val book = realm.where(BookObject::class.java).equalTo("id", id).findFirst()
        realm.executeTransaction {
            val obj = it.createObject(ActionPlanObject::class.java, UUID.randomUUID().toString())
            obj.title = titleEditText.text.toString()
            obj.what = whatEditText.text.toString()
            obj.could = couldEditText.text.toString()
            obj.couldNot = couldNotEditText.text.toString()
            obj.nextAction = actionNextEditText.text.toString()
            book?.actionPlanDairy?.add(obj)
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
        menuSaveButton = saveItem.actionView.findViewById(R.id.saveButton)
        menuSaveButton.setOnClickListener {
            // 入力した情報を保存
            saveData()

            // キーボードの非表示
            val inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // 登録に成功したら、Alertを表示
            AlertDialog.Builder(it.context, R.style.BasicDialogTheme)
                .setMessage("登録に成功しました。")
                .setPositiveButton("OK") { dialog, which ->
                    activity?.supportFragmentManager?.popBackStack()
                }.show()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        menuSaveButton.isEnabled = isInput()
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
    }
}

