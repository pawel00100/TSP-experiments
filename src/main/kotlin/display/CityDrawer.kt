@file:Suppress("jol")

package display

import model.City
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel


class CityDrawer(
    var simulationRadius: Double,
    cities: List<City>,
    routes: Collection<Pair<City, City>>,
    val chosenRoutes: MutableCollection<Pair<City, City>>,
    val title: String = ""
) {
//    var windowSize = 1000
    var windowSize = 800
    var cities: List<City>
    var routes: Collection<Pair<City, City>>
    var frame: Frame

    init {
        frame = Frame()
        this.cities = cities
        this.routes = routes
    }

    fun repaint() {
        frame.repaint()
    }

    inner class Frame : JFrame(title) {
        var panel: Panel

        init {
            panel = Panel()
            defaultCloseOperation = EXIT_ON_CLOSE
            this.add(panel)
            pack()
            setLocationRelativeTo(null)
            this.isVisible = true
        }
    }

    inner class Panel : JPanel(true) {
        init {
            this.preferredSize = Dimension(windowSize, windowSize)
        }

        override fun paint(g: Graphics) {
            val g2D = g as Graphics2D
            g2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )
            g2D.paint = Color.black
            g2D.drawRect(0, 0, windowSize, windowSize)
            g2D.fillRect(0, 0, windowSize, windowSize)

            if (cities.size <= 50) {
                g2D.paint = Color.orange
                g2D.stroke = BasicStroke(.1f);
                drawRoutes(g2D, routes)
            }

            g2D.paint = Color.red
            g2D.stroke = BasicStroke(2f);
            drawRoutes(g2D, chosenRoutes)

            g2D.paint = Color.orange
            drawCities(g2D)
        }
    }

    private fun drawCities(g2D: Graphics2D) {
        val cityRadius = 10 // higher number more equal
        for ((x, y) in cities) {
            val xPos = ((0.5 + x / simulationRadius / 2) * windowSize).toInt()
            val yPos = ((0.5 + y / simulationRadius / 2) * windowSize).toInt()
            g2D.drawOval(xPos - cityRadius / 2, yPos - cityRadius / 2, cityRadius, cityRadius)
            g2D.fillOval(xPos - cityRadius / 2, yPos - cityRadius / 2, cityRadius, cityRadius)
        }
    }

    private fun drawRoutes(g2D: Graphics2D, routes: Collection<Pair<City, City>>) {
        for ((c1, c2) in routes) {
            if (c1 == c2) {
                continue
            }

            val xPos = ((0.5 + c1.x / simulationRadius / 2) * windowSize).toInt()
            val yPos = ((0.5 + c1.y / simulationRadius / 2) * windowSize).toInt()
            val xPos2 = ((0.5 + c2.x / simulationRadius / 2) * windowSize).toInt()
            val yPos2 = ((0.5 + c2.y / simulationRadius / 2) * windowSize).toInt()

            g2D.drawLine(xPos, yPos, xPos2, yPos2)
        }
    }

    fun saveToPng(fileName: String) {
        val bufferedImage = BufferedImage(frame.panel.getWidth(), frame.panel.getHeight(), BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()
        frame.panel.paintAll(graphics)

        val filenameWithFiletype = if (fileName.endsWith(".png")) {
            fileName
        } else {
            "$fileName.png"
        }

        try {
            ImageIO.write(bufferedImage, "png", File(filenameWithFiletype))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val cities = Arrays.asList(
                City(0.0, 0.0),
                City(1.0, 0.0),
                City(-1.0, 0.4)
            )

            val routes = cities.cartesianProduct(cities)
            val chosenRoutes = mutableListOf(routes[0], routes[1])

            CityDrawer(2.0, cities, routes, chosenRoutes).saveToPng("example")
        }
    }
}

fun <T, S> Collection<T>.cartesianProduct(other: Iterable<S>): List<Pair<T, S>> {
    return this.flatMap { first -> other.map { second -> first to second } }
}
