package rpt.tool.mementobibere.ui.faq

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentFaqBinding
import rpt.tool.mementobibere.utils.data.appmodel.FAQModel
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.custom.AnimationUtils


class FAQFragment : BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {

    var lst_faq: MutableList<FAQModel> = ArrayList()
    var answer_block_lst: MutableList<LinearLayout> = ArrayList()
    var img_faq_lst: MutableList<ImageView> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        body()
    }

    private fun body() {
        binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_faqs)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        setFAQData()
        loadFAQData()
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    private fun setFAQData() {
        var faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_1)
        faqModel.answer = requireContext().getString(R.string.faq_answer_1)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_2)
        faqModel.answer = requireContext().getString(R.string.faq_answer_2)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_3)
        faqModel.answer = requireContext().getString(R.string.faq_answer_3)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_12)
        faqModel.answer = requireContext().getString(R.string.faq_answer_12)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_13)
        faqModel.answer = requireContext().getString(R.string.faq_answer_13)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_4)
        faqModel.answer = requireContext().getString(R.string.faq_answer_4)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_11)
        faqModel.answer = requireContext().getString(R.string.faq_answer_11)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_5)
        faqModel.answer = requireContext().getString(R.string.faq_answer_5)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_6)
        faqModel.answer = requireContext().getString(R.string.faq_answer_6)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_7)
        faqModel.answer = requireContext().getString(R.string.faq_answer_7)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_8)
        faqModel.answer = requireContext().getString(R.string.faq_answer_8)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_9)
        faqModel.answer = requireContext().getString(R.string.faq_answer_9)
        lst_faq.add(faqModel)

        faqModel = FAQModel()
        faqModel.question = requireContext().getString(R.string.faq_question_14)
        faqModel.answer = requireContext().getString(R.string.faq_answer_14)
        lst_faq.add(faqModel)
    }

    @SuppressLint("InflateParams")
    private fun loadFAQData() {
        binding.faqBlock.removeAllViews()
        for (k in lst_faq.indices) {
            val pos = k

            val rowModel = lst_faq[k]

            val layoutInflater = LayoutInflater.from(requireContext())
            val itemView: View = layoutInflater.inflate(R.layout.row_item_faq, null, false)

            val lbl_question = itemView.findViewById<AppCompatTextView>(R.id.lbl_question)
            val lbl_answer = itemView.findViewById<AppCompatTextView>(R.id.lbl_answer)

            val question_block = itemView.findViewById<LinearLayout>(R.id.question_block)
            val answer_block = itemView.findViewById<LinearLayout>(R.id.answer_block)
            val img_faq = itemView.findViewById<ImageView>(R.id.img_faq)

            answer_block_lst.add(answer_block)
            img_faq_lst.add(img_faq)

            lbl_question.text = rowModel.question
            lbl_answer.text = rowModel.answer

            question_block.setOnClickListener {
                if (answer_block.visibility == View.GONE) {
                    viewAnswer(pos)
                    img_faq.setImageResource(R.drawable.ic_faq_minus)
                    AnimationUtils.expand(answer_block)
                } else {
                    img_faq.setImageResource(R.drawable.ic_faq_plus)
                    AnimationUtils.collapse(answer_block)
                }
            }

            binding.faqBlock.addView(itemView)
        }
    }

    private fun viewAnswer(pos: Int) {
        for (k in answer_block_lst.indices) {
            if (k == pos) continue
            else {
                img_faq_lst[k].setImageResource(R.drawable.ic_faq_plus)
                AnimationUtils.collapse(answer_block_lst[k])
            }
        }
    }
}