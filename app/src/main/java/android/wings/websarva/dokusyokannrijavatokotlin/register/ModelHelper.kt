package android.wings.websarva.dokusyokannrijavatokotlin.register


object ModelHelper {
    private var bookData = BookHelper()
    private var loadedUserData = LoadedUserInfo()


    // データを格納
    fun setData(bookData: BookHelper, loadedUserData: LoadedUserInfo) {
        this.bookData = bookData
        this.loadedUserData = loadedUserData
    }

    fun resetData() {
        this.bookData = BookHelper()
        this.loadedUserData = LoadedUserInfo()
    }

    // 選択した投稿を取得
    fun getPostData(): BookHelper {
        return bookData
    }

    // 取得
    fun getLoadedUserData(): LoadedUserInfo {
        return loadedUserData
    }



}