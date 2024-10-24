package com.example.happybirthday

import ItemsViewModel
import LoginViewModel
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.happybirthday.data.AppDatabase
import com.example.happybirthday.data.Item
import com.example.happybirthday.ui.theme.HappyBirthdayTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.*
import com.example.happybirthday.NotificationHelper


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private lateinit var database: AppDatabase
    private lateinit var notificationHelper: NotificationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the NotificationHelper
        notificationHelper = NotificationHelper(this, this)

        // Initialize the database
        database = AppDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                Column (modifier = Modifier
                    .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                )
                {
                    MyApp()
                }

            }
        }

//        // Example of using the database in a coroutine
//        lifecycleScope.launch {
//            // Insert an item into the database
//            val item = Item(
//                title = "GalaxySmart 2000",
//                description = "A high-end smartphone with a 6.5-inch AMOLED display, 128GB storage, and a 48MP camera.",
//                quantity = 50,
//                price = 799.99f
//            )
//
//            database.itemDao().insertItem(item)
//
//            //Insert user into database
//            val user = User(
//                username = "Logan5577",
//                firstName = "Logan",
//                lastName = "Riedell",
//                passWord = "birchTree223"
//            )
//            database.userDao().insertUser(user)
//    }
//        val sampleItems = listOf(
//            Item(title = "Laptop", description = "Gaming Laptop", quantity = 5, price = 1200.99f),
//            Item(title = "Smartphone", description = "Android Smartphone", quantity = 10, price = 699.99f),
//            Item(title = "Headphones", description = "Noise-cancelling headphones", quantity = 15, price = 199.99f),
//            Item(title = "Tablet", description = "Android Tablet", quantity = 8, price = 499.99f),
//            Item(title = "Monitor", description = "4K UHD Monitor", quantity = 7, price = 299.99f)
//        )
//
//        for(item in sampleItems) {
//            CoroutineScope(Dispatchers.IO).launch {
//                database.itemDao().insertItem(item)
//                }
//        }
    }

    @Composable
    fun MyApp(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        var loginViewModel = LoginViewModel(database)
        var itemsViewModel = ItemsViewModel(database.itemDao())
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = modifier // Apply the modifier here if needed
        ) {
            composable("login") {
                LoginScreen(navController, loginViewModel)
            }
            composable("details") {
                DetailScreen(name = "Logan", navController, loginViewModel)
            }
            composable("items_list"){
                ItemsScreen(notificationHelper, viewModel = itemsViewModel, navController, itemsViewModel)
            }
            composable("item_detail") {
                val selectedItem = itemsViewModel.selectedItem
                selectedItem?.let {
                    ItemDetailScreen(item = it, navController = navController)
                } ?: run {
                    // Handle the case where the selectedItem isdd null (optional)
                    println("Error: No item selected")
                }
            }
            composable("edit_item") {
                val selectedItem = itemsViewModel.selectedItem
                EditItemScreen(item = selectedItem, navController = navController, itemsViewModel=itemsViewModel)
            }
        }
    }

    @Composable
    fun DetailScreen(name: String, navController: NavController, viewModel : LoginViewModel){

        // Use DisposableEffect to wipe the data when the composable is leaving the composition
        DisposableEffect(Unit) {
            onDispose {
                viewModel.resetData()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Blue, Color.Green),
                        start = Offset(200f, 800f),
                        end = Offset(
                            Float.POSITIVE_INFINITY,
                            Float.POSITIVE_INFINITY
                        ) // Cover the entire screen
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Inventory",
                fontSize = 40.sp,
                color = Color.White
            )
            Text(
                text = viewModel.username,
                fontSize = 24.sp,
                color = Color.White
            )
            Text(
                text = viewModel.password,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                navController.navigate("login")
            }) {
                Text("Go Back to Home Screen")
            }
        }

    }

    @Composable
    fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

        // Local state for username and password
        var username by remember { mutableStateOf(viewModel.username) }
        var password by remember { mutableStateOf(viewModel.password) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Blue, Color.Green),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Inventory",
                fontSize = 40.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(40.dp)
            )

            // Heading for login or create user
            val heading = if (viewModel.isExistingUser) "Login" else "Create New User"
            Text(
                text = heading,
                fontSize = 30.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Username TextField
            TextField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.username = it // Sync local state with ViewModel
                },
                label = { Text("Username") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            )

            // Password TextField
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.password = it // Sync local state with ViewModel
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    if (viewModel.isExistingUser) {
                        // Handle login logic
                        viewModel.loginUser(
                            onSuccess = {
                                navController.navigate("items_list")
                            },
                            onFailure = {
                                println("Invalid username or password")
                            }
                        )
                    } else {
                        // Handle user registration logic
                        viewModel.registerUser(
                            onSuccess = {
                                navController.navigate("items_list")
                            },
                            onFailure = {
                                println("Username already exists")
                            }
                        )
                    }
                }
            }) {
                Text(if (viewModel.isExistingUser) "Login" else "Create Account")
            }

            // Switch to create new user or login mode
            TextButton(onClick = {
                viewModel.isExistingUser = !viewModel.isExistingUser // Toggle between login and create user
            }) {
                Text(if (viewModel.isExistingUser) "Don't Have An Account?" else "Already Have An Account?")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ItemsScreen(
        notificationHelper: NotificationHelper,
        viewModel: ItemsViewModel,
        navController: NavController,
        itemsViewModel: ItemsViewModel
    ) {
        // Observe the lowStockItems directly from the ViewModel's mutable state
        val lowStockItems = itemsViewModel.lowStockItems

        // Call the function to load low stock items once when the composable is first loaded
        LaunchedEffect(Unit) {
            itemsViewModel.checkForLowStockItems()
        }

        // Load items from the ViewModel
        LaunchedEffect(Unit) {
            viewModel.loadItems()
        }

        // Use Column to display the list of items
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Blue, Color.Green),
                        start = Offset(0f, 0f),
                        end = Offset(
                            Float.POSITIVE_INFINITY,
                            Float.POSITIVE_INFINITY
                        ) // Cover the entire screen
                    )
                ),
        ) {
            Text(
                text = "Items List",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp, top = 50.dp)
            )
            ItemList(
                items = viewModel.items,
                navController = navController,
                itemsViewModel = itemsViewModel,
                onItemClick = { item ->
                    println("Item clicked: ${item.title}")
                }
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(), // Full screen container
            contentAlignment = Alignment.BottomEnd // Align content to bottom right
        ) {
            FloatingActionButton(
                onClick = {
                    itemsViewModel.selectItem(null)
                    navController.navigate("edit_item")
                },
                modifier = Modifier
                    .padding(50.dp) // Space from edges
                    .size(56.dp), // Size of the FAB
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // Plus sign icon
                    contentDescription = "Add Item", // Accessibility description
                    tint = Color.Blue // Optional: change icon color
                )
            }
        }

        // After items are loaded, notify the user if low-stock items exist
        if (lowStockItems.isNotEmpty()) {
            lowStockItems.forEach { item ->
                notificationHelper.sendOutOfStockNotification(item.title)
            }
        }
    }


@Composable
fun ItemDetailScreen(item: Item, modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Quantity: ${item.quantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = ("$${item.price}"),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("edit_item")},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Edit")
        }
    }
}

// Composable to display the list of items
@Composable
fun ItemList(items: List<Item>, navController: NavController, itemsViewModel: ItemsViewModel, onItemClick: (Item) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            ItemRow(item = item, navController, itemsViewModel)
        }
    }
}

// Composable for each row (item) in the list
@Composable
fun ItemRow(item: Item, navController: NavController, itemsViewModel: ItemsViewModel) {
    var expanded by remember { mutableStateOf(false) } //
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                itemsViewModel.selectItem(item)
                navController.navigate("item_detail")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Quantity: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Trash bin button with dropdown
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = Color.Blue // Optional: tint the trash bin red
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (300).dp, y =-100.dp) // Adjust horizontal offset to shift menu
            ) {
                DropdownMenuItem(text = { Text(text = "Delete") }, onClick = {
                    itemsViewModel.deleteItem(item)
                    navController.navigate("items_list")
                })
            }
        }
    }
}
@Composable
fun EditItemScreen(
    item: Item?, // Nullable to differentiate between new and existing items
    navController: NavController,
    itemsViewModel: ItemsViewModel,
    // onItemSaved: () -> Unit // Callback to navigate or give feedback after saving
) {
    var title by remember { mutableStateOf(item?.title ?: "") } // Empty string for new items
    var description by remember { mutableStateOf(item?.description ?: "") }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "0") }
    var price by remember { mutableStateOf(item?.price?.toString() ?: "0.0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = if (item == null) "Add New Item" else "Edit Item", style = MaterialTheme.typography.titleLarge)

        // Title
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default
        )

        // Description
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default
        )

        // Quantity
        TextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Price
        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Convert quantity and price to their respective types
                val newItem = Item(
                    itemId = item?.itemId ?: 0, // For new items, this should be 0 (or auto-generated)
                    title = title,
                    description = description,
                    quantity = quantity.toIntOrNull() ?: 0,
                    price = price.toFloatOrNull() ?: 0f
                )

                // Check if we're adding a new item or updating an existing one
                CoroutineScope(Dispatchers.IO).launch {
                    if (item == null) {
                        // Insert new item if it's a new item
                        itemsViewModel.itemDao.insertItem(newItem)
                    } else {
                        // Update existing item
                        itemsViewModel.itemDao.updateItem(newItem)
                    }
                    withContext(Dispatchers.Main) {
                        // Navigate back or notify UI
                        navController.popBackStack() // Or any other navigation logic
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(40.dp)
        ) {
            Text(if (item == null) "Add" else "Save")
        }
    }
}


//Validates user and password input
suspend fun validate(username: String, password: String, database: AppDatabase): Boolean {
    return withContext(Dispatchers.IO) {
        val user = database.userDao().getUserByUsername(username.trim())
        if (user != null) {
            user.passWord == password.trim()
        } else {
            println("User not found")
            false
        }
    }
}
    }


