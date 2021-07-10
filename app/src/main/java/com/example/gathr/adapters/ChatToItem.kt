package com.example.gathr.adapters

import android.view.View
import com.example.gathr.R
import com.example.gathr.databinding.ChatToRowBinding
import com.example.gathr.models.User
import com.xwray.groupie.viewbinding.BindableItem

class ChatToItem(val text:String , val user: User):BindableItem<ChatToRowBinding>() {
    override fun bind(viewBinding: ChatToRowBinding, position: Int) {
        viewBinding.tvToRow.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun initializeViewBinding(view: View): ChatToRowBinding {
        return ChatToRowBinding.bind(view)
    }
}