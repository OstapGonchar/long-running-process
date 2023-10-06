import engine.Engine
import request.Requestor
import status.StatusObserver

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val typeOfService = args[0]
        when (typeOfService) {
            "engine" -> startEngine(args[1])
            "producer" -> startProducer()
            "status" -> startStatus()
            else -> throw Exception("This type is not yet supporte")
        }
    }

}

fun startProducer() {
    val producingSpeedSeconds = 30
    val requestor = Requestor()
    while (true) {
        requestor.sendRequest()
        Thread.sleep(producingSpeedSeconds.toLong() * 1000)
    }
}

fun startEngine(engineNumber: String) {
    val engineSpeedSeconds = 60
    Engine(engineNumber.toInt(), engineSpeedSeconds).start()
}

fun startStatus() {
    val statusCheckSeconds = 5
    val statusObserver = StatusObserver()
    while (true) {
        statusObserver.printCurrentStatus()
        Thread.sleep(statusCheckSeconds.toLong() * 1000)
    }
}
