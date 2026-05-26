package com.example

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dobadoba", appName)
  }

  @Test
  fun `verify application package name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val packageName = context.packageName
    assertNotNull(packageName)
    assertEquals(true, packageName.contains("example") || packageName.contains("aistudio"))
  }
}
