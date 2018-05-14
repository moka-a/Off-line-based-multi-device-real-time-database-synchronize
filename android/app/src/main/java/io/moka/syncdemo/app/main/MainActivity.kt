package io.moka.syncdemo.app.main

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import io.moka.base.component.BaseActivity
import io.moka.base.module.radius
import io.moka.syncdemo.R
import io.moka.syncdemo.app.main.dialog.PostQuestionDialog
import io.moka.syncdemo.app.main.dialog.SetUserDialog
import io.moka.syncdemo.component.UserManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk15.coroutines.onClick
import java.util.*
import kotlin.properties.Delegates

class MainActivity : BaseActivity() {

    private val presenter by lazy { MainPresenter(this) }
    private val viewModel by lazy { ViewModel() }
    private val adapter by lazy { QnaAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        loadData()
    }

    private fun initView() {
        /* recyclerView */
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        /* coordinator layout */
        appBarlayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (isFinishing)
                return@addOnOffsetChangedListener
            imageView_profile.alpha = 1 - (verticalOffset.toFloat() / appBarLayout.totalScrollRange) * -1
            textView_say.alpha = 1 - (verticalOffset.toFloat() / appBarLayout.totalScrollRange) * -1
            textView_name.alpha = 1 - (verticalOffset.toFloat() / appBarLayout.totalScrollRange) * -1
        }

        /* bind events */
        textView_name.onClick { onClickToSetUser() }
        textView_say.onClick { onClickToSetUser() }
        floatingActionButton_add.onClick { onClickToPostQuestion() }
        adapter.onClickItem = { onClickToDetail(it) }
    }

    private fun loadData() {
        presenter.loadData()

        viewModel.currentName = UserManager.currentName
        viewModel.currentSay = UserManager.currentSay
        viewModel.currentProfileUrl = UserManager.currentImageUrl
    }

    /**
     * Click listener
     */

    private fun onClickToPostQuestion() {
        PostQuestionDialog()
                .showDialog(supportFragmentManager, { text: String ->
                    presenter.insertQuestion(text)
                })
    }

    private fun onClickToSetUser() {
        SetUserDialog()
                .apply {
                    name = viewModel.currentName
                    say = viewModel.currentSay
                }
                .showDialog(supportFragmentManager, { name, say ->
                    viewModel.currentName = name
                    viewModel.currentSay = say

                    UserManager.currentName = name
                    UserManager.currentSay = say
                })
    }

    private fun onClickToDetail(qna: QnaAdapter.Data?) {

    }

    /**
     */

    fun refresh() {
        adapter.clear()
        presenter.loadData()
    }

    fun setAdapter(qnaList: ArrayList<QnaAdapter.Data>) {
        adapter.items = qnaList
    }

    /**
     * ViewModel
     */

    inner class ViewModel {

        var currentName: String by Delegates.observable("",
                { _, _, new ->
                    if (!new.isEmpty())
                        textView_name.text = new
                    else
                        textView_name.text = "이름을 입력해주세요"
                })

        var currentSay: String by Delegates.observable("",
                { _, _, new ->
                    if (!new.isEmpty())
                        textView_say.text = new
                    else
                        textView_say.text = "간단한 소개를 입력해 주세요"
                })

        var currentProfileUrl: String by Delegates.observable("",
                { _, _, new ->
                    if (new.isEmpty())
                        imageView_profile.radius(this@MainActivity, R.drawable.vc_profile_gray, 28)
                    else
                        imageView_profile.radius(this@MainActivity, new, 28)
                })

    }

}
