package util

import model.City

fun calculateDistance2(city1: City, city2: City): Double {
    return Math.sqrt((city2.x - city1.x)*(city2.x - city1.x) + (city2.y - city1.y) * (city2.y - city1.y))
}