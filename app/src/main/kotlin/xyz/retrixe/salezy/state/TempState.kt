package xyz.retrixe.salezy.state

import xyz.retrixe.salezy.api.entities.Customer
import xyz.retrixe.salezy.api.entities.GiftCard
import xyz.retrixe.salezy.api.entities.InventoryItem

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

    val inventoryItems = listOf(
        InventoryItem("Vase", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 1, "N/A", 100, 10),
        InventoryItem("cHOROCOLATE", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 2, "N/A", 200, 20),
        InventoryItem("THING", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 3, "N/A", 300, 30),
        InventoryItem("minecrasft", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 4, "N/A", 400, 40),
        InventoryItem("house", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 5, "N/A", 500, 50),
        InventoryItem("life", "https://cdn.discordapp.com/stickers/1224711111098499193.png", 6, "N/A", 600, 60),
        InventoryItem("nextrjs", null, 7, "N/A", 700, 70),
        InventoryItem("jwt", null, 8, "N/A", 800, 80),
        InventoryItem("Phone", null, 9, "N/A", 900, 90),
    )

    val customers = listOf(
        Customer(1, "(293)-023-3921", "John Doe", "test@gmail.com", "51 E Blvd", "N/A"),
        Customer(2, "(493)-313-3851", "John Doe", "test@gmail.com", "51 E Blvd", "N/A"),
        Customer(3, "(294)-085-3311", "John Doe", "test@gmail.com", "51 E Blvd", "N/A"),
    )
}
