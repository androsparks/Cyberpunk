package cyberpunk.core.transition.types

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import cyberpunk.core.WORLD_HEIGHT
import cyberpunk.core.WORLD_WIDTH
import cyberpunk.core.transition.Transition

/**
 * The Left-to-Right or Right-to-Left sliding motions for the [HorizontalSlide].
 * Explicitly passed on the class' constructor.
 * Note that each of the four Vector2 values below are correspondent to the bottom-left
 * corners of the states. In other words, currentInitial can be read as "currentState's
 * Initial Bottom-Left Coordinate", whilst currentFinal can be read as "currentState's
 * Final Bottom-Left Coordinate" (at the end of the transition).
 */
enum class Motion (
  val currentInitial: Vector2,
  val nextInitial: Vector2,
  val currentFinal: Vector2,
  val nextFinal: Vector2
) {
  RIGHT_TO_LEFT(Vector2(), Vector2(WORLD_WIDTH.toFloat(), 0f), Vector2(-WORLD_WIDTH.toFloat(), 0f), Vector2()),
  LEFT_TO_RIGHT(Vector2(), Vector2(-WORLD_WIDTH.toFloat(), 0f), Vector2(WORLD_WIDTH.toFloat(), 0f), Vector2())
}

const val DEFAULT_LERP = 0.2f

class HorizontalSlide
  @JvmOverloads
  constructor(private val motion: Motion, private val lerp: Float = DEFAULT_LERP) : Transition {

  /**
   * Overrides the property defined at the [Transition] interface.
   * @property [Transition.running]
   */
  override var running = false

  /**
   * Current position, in the form of a [Vector2], of the current state.
   */
  private val currentStateCurrentPos = motion.currentInitial

  /**
   * Current position, in the form of a [Vector2], of the next state.
   */
  private val nextStateCurrentPos = motion.nextInitial

  /**
   * Overrides the default [Transition.completed] function, declared
   * and implemented on the [Transition] interface.
   * @return whether or not the transition is finished.
   */
  override fun completed() = targetPositionReached()

  /**
   * @see [Transition.update]
   */
  override fun update(delta: Float) {
    if (!running) return
    currentStateCurrentPos.lerp(motion.currentFinal, lerp)
    nextStateCurrentPos.lerp(motion.nextFinal, lerp)
  }

  /**
   * @see [Transition.render]
   */
  override fun render(batch: Batch, current: TextureRegion, next: TextureRegion) {
    batch.begin()
    batch.draw(current, currentStateCurrentPos.x, currentStateCurrentPos.y, WORLD_WIDTH.toFloat(), WORLD_HEIGHT.toFloat())
    batch.draw(next, nextStateCurrentPos.x, nextStateCurrentPos.y, WORLD_WIDTH.toFloat(), WORLD_HEIGHT.toFloat())
    batch.end()
  }

  /**
   * Because [Vector2.lerp] is being used, we can't expect it to
   * reach exactly a given target position - hence why the need
   * to define a (small-ish) error margin.
   * @return whether or not the target position can be understood as finished.
   */
  private fun targetPositionReached(): Boolean {
    val errorMargin = 0.1f
    return Math.abs(nextStateCurrentPos.x) <= errorMargin
  }
}