data class Product(val name: String, val price: Double, val quantity: Int)

enum class SortOption{
    PRICE_ASCENDING,
    PRICE_DESCENDING,
    QUANTITY_ASCENDING,
    QUANTITY_DESCENDING
}

class ProductInventory(private val products: List<Product>) {

    fun calculateTotalInventoryValue(): Double {
        return products.sumOf { it.price * it.quantity }
    }

    fun findMostExpensiveProduct(): String? {
        return products.maxByOrNull { it.price }?.name
    }

    fun isProductInStock(productName: String): Boolean {
        return products.any { it.name == productName && it.quantity > 0 }
    }
    fun sortBy(option: SortOption): List<Product> {
        return when (option) {
            SortOption.PRICE_ASCENDING -> products.sortedBy { it.price }
            SortOption.PRICE_DESCENDING -> products.sortedByDescending { it.price }
            SortOption.QUANTITY_ASCENDING -> products.sortedBy { it.quantity }
            SortOption.QUANTITY_DESCENDING -> products.sortedByDescending { it.quantity }
        }
    }
}

fun main() {
    val products = listOf(
        Product("Laptop", 999.99, 5),
        Product("Smartphone", 499.99, 10),
        Product("Tablet", 299.99, 0),
        Product("Smartwatch", 199.99, 3)
    )

    val inventory = ProductInventory(products)

    println("Total Inventory Value: %.2f".format(inventory.calculateTotalInventoryValue()))
    println("Most Expensive Product: \"${inventory.findMostExpensiveProduct()}\"")
    println("Headphones in Stock: ${inventory.isProductInStock("Headphones")}")
    println("Products sorted by price (ascending): ${inventory.sortBy(SortOption.PRICE_ASCENDING)}")
    println("Products sorted by price (descending): ${inventory.sortBy(SortOption.PRICE_DESCENDING)}")
    println("Products sorted by quantity (ascending): ${inventory.sortBy(SortOption.QUANTITY_ASCENDING)}")
    println("Products sorted by quantity (descending): ${inventory.sortBy(SortOption.QUANTITY_DESCENDING)}")
}
