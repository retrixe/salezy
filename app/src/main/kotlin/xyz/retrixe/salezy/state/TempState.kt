package xyz.retrixe.salezy.state

import xyz.retrixe.salezy.api.entities.*
import java.time.Instant

// FIXME: Remove all references to these states!
object TempState {
    val giftCards = listOf(
        GiftCard("EUI2FVEU", 10, 10, 0, 0),
        GiftCard("UIFBH2HD", 20, 20, 0, 0),
        GiftCard("BEFW9HSS", 30, 30, 0, 0),
        GiftCard("BRIRI329", 40, 40, 0, 0),
        GiftCard("CSDR2831", 50, 50, 0, 0),
        GiftCard("RJEJ2113", 60, 60, 0, 0),
        GiftCard("EOIWOI9R", 70, 70, 0, 0),
        GiftCard("GHR23829", 80, 80, 0, 0),
        GiftCard("FUIRWHIH", 90, 90, 0, 0),
    )

    val inventoryItems = mutableListOf(
        InventoryItem("Vase", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 1, "N/A", 100, 10),
        InventoryItem("Chocolate", null, 2, "N/A", 200, 20),
        InventoryItem("Perfume", null, 3, "N/A", 300, 30),
        InventoryItem("Game", null, 4, "N/A", 400, 40),
        InventoryItem("House", null, 5, "N/A", 500, 50),
        InventoryItem("Light", null, 6, "N/A", 600, 60),
        InventoryItem("Laptop", null, 7, "N/A", 700, 70),
        InventoryItem("Water Bottle", null, 8, "N/A", 800, 80),
        InventoryItem("Phone", "https://rukminim2.flixcart.com/image/850/1000/xif0q/mobile/g/x/9/-original-imaggsudg5fufyte.jpeg?q=90&crop=false", 9, "N/A", 900, 90),
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
            listOf(InvoicedItem(1, 2)),
            null)
    )
}
