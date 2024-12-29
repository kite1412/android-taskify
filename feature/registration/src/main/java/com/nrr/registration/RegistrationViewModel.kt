package com.nrr.registration

import androidx.lifecycle.ViewModel
import com.nrr.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

}