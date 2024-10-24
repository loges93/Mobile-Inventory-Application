

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happybirthday.data.Item
import com.example.happybirthday.data.ItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsViewModel (val itemDao : ItemDao) : ViewModel(){
    // Using mutableStateOf to hold the list of items
    var items by mutableStateOf<List<Item>>(emptyList())
        private set
    var selectedItem by mutableStateOf<Item?>(null)
        private set

    fun selectItem(item: Item?) {
        selectedItem = item
    }
    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO){
            itemDao.deleteItemById(item.itemId)
        }
    }

    // Function to load items from the database
    fun loadItems() {
        viewModelScope.launch {
            val itemList = withContext(Dispatchers.IO) {
                itemDao.getAllItems() // Fetch items from the database
            }
            items = itemList // Update the state
        }
    }

    // Using MutableState to hold low stock items
    var lowStockItems by mutableStateOf<List<Item>>(emptyList())
        private set

    // Function to check for low-stock items and update the state
    fun checkForLowStockItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = itemDao.get_outofstock_items() // Assuming you have this function
            lowStockItems = items  // Update the state with the fetched items
        }
    }

}