package day16.composables

import androidx.compose.ui.semantics.SemanticsPropertyKey

object SemanticPropertyKeys {

    object MazeTile {
        enum class MazeTileState {
            Wall,
            ShortestSolution,
            Start,
            End,
            CurrentPosition,
            OtherSolutions,
            Visited
        }

        val State = SemanticsPropertyKey<List<MazeTileState>>("state")
    }

}