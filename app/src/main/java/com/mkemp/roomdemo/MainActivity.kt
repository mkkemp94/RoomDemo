package com.mkemp.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mkemp.roomdemo.databinding.ActivityMainBinding
import com.mkemp.roomdemo.db.Subscriber
import com.mkemp.roomdemo.db.SubscriberDatabase
import com.mkemp.roomdemo.db.SubscriberRepository

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val viewModelFactory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, viewModelFactory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.subscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        displaySubscribersList()
    }

    private fun displaySubscribersList() {
        subscriberViewModel.subscribers.observe(this, Observer {
            Log.i("MyTag", it.toString())
            binding.subscriberRecyclerView.adapter = MyRecyclerViewAdapter(it, { selectedItem : Subscriber -> listItemClicked(selectedItem)})
        })
    }

    private fun listItemClicked(subscriber: Subscriber) {
        Toast.makeText(this, "selected name ${subscriber.name}", Toast.LENGTH_LONG).show()
    }
}