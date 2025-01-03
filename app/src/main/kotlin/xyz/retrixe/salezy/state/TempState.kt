package xyz.retrixe.salezy.state

import xyz.retrixe.salezy.api.entities.*
import java.time.Instant

object TempState {
    val giftCards = listOf(
        GiftCard("EUI2FVEU", 10, 10, 0, 0, true),
        GiftCard("UIFBH2HD", 20, 20, 0, 0, false),
        GiftCard("BEFW9HSS", 30, 30, 0, 0, false),
        GiftCard("BRIRI329", 40, 40, 0, 0, false),
        GiftCard("CSDR2831", 50, 50, 0, 0, false),
        GiftCard("RJEJ2113", 60, 60, 0, 0, false),
        GiftCard("EOIWOI9R", 70, 70, 0, 0, false),
        GiftCard("GHR23829", 80, 80, 0, 0, false),
        GiftCard("FUIRWHIH", 90, 90, 0, 0, false),
    )

    val inventoryItems = mutableListOf(
        InventoryItem("Vase", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 1, "N/A", 100, 100, 10),
        InventoryItem("Chocolate", null, 2, "N/A", 200, 200, 20),
        InventoryItem("Perfume", null, 3, "N/A", 300, 300, 30),
        InventoryItem("Game", null, 4, "N/A", 400, 400, 40),
        InventoryItem("House", null, 5, "N/A", 500, 500, 50),
        InventoryItem("Light", null, 6, "N/A", 600, 600, 60),
        InventoryItem("Laptop", null, 7, "N/A", 700, 700, 70),
        InventoryItem("Water Bottle", null, 8, "N/A", 800, 800, 80),
        InventoryItem("Phone", "https://rukminim2.flixcart.com/image/850/1000/xif0q/mobile/g/x/9/-original-imaggsudg5fufyte.jpeg?q=90&crop=false", 9, "N/A", 900, 900, 90),
    )

    val customers = mutableListOf(
        Customer(1, "(293)-023-3921", "John Doe", "test@gmail.com", "51 E Blvd", null, "N/A"),
        Customer(2, "(493)-313-3851", "John Doe", "test@gmail.com", "51 E Blvd", null, "N/A"),
        Customer(3, "(294)-085-3311", "John Doe", "test@gmail.com", "51 E Blvd", null, "N/A"),
    )

    val invoices = mutableListOf(
        Invoice(
            32451,
            1345,
            4000,
            4400,
            10,
            Instant.now().toEpochMilli(),
            listOf(InvoicedItem(1, 2, "lol", "lmao", 3000, 4000)),
            1,
            null,
            null,
            null)
    )
}
