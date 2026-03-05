package com.example.modernmessenger.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modernmessenger.databinding.FragmentNewChatBinding
import com.example.modernmessenger.domain.entity.User
import com.example.modernmessenger.domain.namespaces.CHILD_ID
import com.example.modernmessenger.presentation.activity.ChatActivity
import com.example.modernmessenger.presentation.recyclerview.users.UsersAdapter
import com.example.modernmessenger.presentation.viewmodel.NewChatFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewChatFragment : Fragment() {

    private lateinit var loadingAlertDialog: LoadingAlertDialog
    private lateinit var binding: FragmentNewChatBinding
    private val viewModel: NewChatFragmentViewModel by viewModels()

    //лямбда функция для создания чата, когда мы нажимаем на пользователя в списке всех пользователей
    private val createChat = { chatUser:User->
        viewModel.createChat(
            chatUser = chatUser,
            onSuccess = {
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra(CHILD_ID, it)
                startActivity(intent)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewChatBinding.inflate(layoutInflater)

        loadingAlertDialog = LoadingAlertDialog(requireContext())
        loadingAlertDialog.setCancelable(false) //чтобы нельзя было нажать и скрыть диалог загрузки

        loadingAlertDialog.show()
        loadAllUsers()

        //срабатывает когда пользователь вносит какой-то запрос в SearchView
        binding.usersSearch.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let{
                    (binding.allUsersRv.adapter as UsersAdapter).filter(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    (binding.allUsersRv.adapter as UsersAdapter).filter(newText)
                }
                return true
            }

        })

        return binding.root
    }

    private fun loadAllUsers(){
        //получаем пользователей и настраиваем recycler view
        viewModel.users.observe(requireActivity()){users->
            binding.allUsersRv.layoutManager = LinearLayoutManager(requireContext())
            binding.allUsersRv.adapter = UsersAdapter(users, createChat)

            loadingAlertDialog.dismiss()
        }
    }
}