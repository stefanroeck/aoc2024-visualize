package day16

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import util.MazeEvent
import util.MazeEventInvoker
import util.MazeEventSink
import java.time.Duration

class ScopedEventInvoker(
    private val delegate: MazeEventInvoker,
    private val scope: CoroutineScope,
    private val delay: Duration = Duration.ofMillis(50),
    private val isCanceled: () -> Boolean = { false },
) :
    MazeEventInvoker {
    override fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>) {
        if (!isCanceled()) {
            if (!delay.isZero) {
                Thread.sleep(delay)
            }
            scope.launch {
                delegate.fire(event, eventSinks)
            }
        }
    }

}