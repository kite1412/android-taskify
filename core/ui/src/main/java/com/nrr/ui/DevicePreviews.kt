package com.nrr.ui

import androidx.compose.ui.tooling.preview.Preview

//devices reference from preview.Device docs

@Preview(name = "phone", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "landscape", device = "spec:width=891dp,height=411dp,dpi=420")
@Preview(name = "foldable", device = "spec:width=673dp,height=841dp,dpi=420")
@Preview(name = "tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class DevicePreviews