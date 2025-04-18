package uz.rezanen.task.presentation.addCard.vm

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.viewmodel.container
import uz.rezanen.task.domain.CardRepository
import uz.rezanen.task.shp.Shp


class AddCardVMImpl(
    private val shp: Shp,
    private val cardRepository:CardRepository,
) : AddCardVM, ViewModel() {
    override fun onEventDispatcher(intent: AddCardIntent) = intent {
        when (intent) {
            is AddCardIntent.SubmitButton -> {

            }
        }


    }


    override val container: Container<AddCardUIState, AddCardSideEffect> =
        container(AddCardUIState.Success)


}