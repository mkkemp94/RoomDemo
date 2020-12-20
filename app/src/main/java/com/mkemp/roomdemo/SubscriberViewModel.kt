package com.mkemp.roomdemo

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkemp.roomdemo.db.Subscriber
import com.mkemp.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(), Observable {

    val subscribers = repository.subscribers

    private var inUpdateOrDeleteMode = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()
    @Bindable
    val inputEmail = MutableLiveData<String>()
    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()
    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdateClicked() {
        if (inUpdateOrDeleteMode) {
            // Update
            subscriberToUpdateOrDelete.name = inputName.value!!
            subscriberToUpdateOrDelete.email = inputEmail.value!!
            update(subscriberToUpdateOrDelete)
        } else {
            // Save
            val name = inputName.value!!
            val email = inputEmail.value!!
            insert(Subscriber(0, name, email))
            inputName.value = null
            inputEmail.value = null
        }
    }

    fun clearAllOrDeleteClicked() {
        if (inUpdateOrDeleteMode) {
            // Delete
            delete(subscriberToUpdateOrDelete)
        } else {
            // Clear All
            clearAll()
        }
    }

    fun insert(subscriber: Subscriber) {
        viewModelScope.launch {
            val newRowId = repository.insert(subscriber)
            if (newRowId > -1) {
                statusMessage.value = Event("Subscriber inserted successfully $newRowId")
            } else {
                statusMessage.value = Event("An error occurred inserting subscriber")
            }
        }
    }

    fun update(subscriber: Subscriber) {
        viewModelScope.launch {
            val numRows = repository.update(subscriber)
            if (numRows > 0) {
                statusMessage.value = Event("$numRows rows updated successfully")

                inputName.value = null
                inputEmail.value = null
                inUpdateOrDeleteMode = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
            }
            else {
                statusMessage.value = Event("An error occurred updating this subscriber")
            }
        }
    }

    fun delete(subscriber: Subscriber) {
        viewModelScope.launch {
            val numRows = repository.delete(subscriber)
            if (numRows > 0) {
                statusMessage.value = Event("$numRows rows deleted successfully")

                inputName.value = null
                inputEmail.value = null
                inUpdateOrDeleteMode = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
            } else {
                statusMessage.value = Event("An  error occurred deleting subscriber")
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            val numRows = repository.deleteAll()
            if (numRows > 0) {
                statusMessage.value = Event("$numRows rows deleted successfully")
            } else {
                statusMessage.value = Event("An error occurred clearing all subscribers")
            }
        }
    }

    fun initUpdateAndDeleteMode(subscriber: Subscriber) {
        inUpdateOrDeleteMode = true
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}