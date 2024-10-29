fun missingNumber(nums: IntArray): Int {
    val n = nums.size
    val idealSum = (n + 1) * (n + 2) / 2 // ranging from 1 to n + 1
    return idealSum - nums.sum()
}

fun main(){
    val nums = intArrayOf(3, 7, 1, 2, 6, 4)
    println(missingNumber(nums))
}