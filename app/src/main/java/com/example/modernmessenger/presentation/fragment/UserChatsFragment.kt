package com.example.modernmessenger.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modernmessenger.databinding.FragmentChatsBinding
import com.example.modernmessenger.presentation.recyclerview.chats.ChatsAdapter
import com.example.modernmessenger.presentation.viewmodel.UserChatsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserChatsFragment : Fragment() {

    private lateinit var loadingAlertDialog: LoadingAlertDialog
    private lateinit var binding: FragmentChatsBinding
    private val viewModel: UserChatsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)

        loadingAlertDialog = LoadingAlertDialog(requireContext())
        loadingAlertDialog.setCancelable(false)

        loadingAlertDialog.show()
        receiveChats()

        return binding.root
    }

    private fun receiveChats(){
        //отображаем все чаты пользователя
        viewModel.chats.observe(requireActivity()){chats->
            binding.userChatsRv.layoutManager = LinearLayoutManager(requireContext())
            binding.userChatsRv.adapter = ChatsAdapter(chats = chats)
            loadingAlertDialog.dismiss() //по окончании загрузки скрываем диалог загрузки
        }
    }
}