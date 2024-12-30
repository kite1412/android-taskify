package com.nrr.registration.model

internal sealed interface FieldAction {
    data object Previous : FieldAction
    data object Next : FieldAction
    data object Complete : FieldAction
}