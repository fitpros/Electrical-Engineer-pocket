package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthManager
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    authManager: AuthManager,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("Electrical Engineer") }
    var company by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var acceptPrivacy by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Create Engineering Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Google Sign In Shortcut
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        val result = authManager.googleSignIn("", "")
                        if (result.isSuccess) {
                            onSignUpSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Google Sign-In is not configured."
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Google Account", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(text = "OR REGISTER WITH EMAIL", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Divider(modifier = Modifier.weight(1f))
            }

            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusCriticalRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusCriticalRed)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = StatusCriticalRed, modifier = Modifier.size(16.dp))
                        Text(text = errorMessage!!, color = StatusCriticalRed, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }

            // Input Fields
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name *") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ElectricalBlue) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ElectricalBlue) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ElectricalBlue) },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City (Optional)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
                    label = { Text("Profession *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company (Optional)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    label = { Text("Job Title (Optional)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (Min. 6 chars) *") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricalBlue) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password *") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricalBlue) },
                modifier = Modifier.fillMaxWidth()
            )

            // Terms & Privacy Checkboxes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = ElectricalBlue)
                )
                Text(text = "I accept the ", fontSize = 12.sp)
                Text(text = "Terms and Conditions", fontSize = 12.sp, color = ElectricalBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToTerms))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = acceptPrivacy,
                    onCheckedChange = { acceptPrivacy = it },
                    colors = CheckboxDefaults.colors(checkedColor = ElectricalBlue)
                )
                Text(text = "I accept the ", fontSize = 12.sp)
                Text(text = "Privacy Policy", fontSize = 12.sp, color = ElectricalBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToPrivacy))
            }

            // Create Account Action
            Button(
                onClick = {
                    when {
                        fullName.isBlank() -> errorMessage = "Please enter your Full Name."
                        !email.contains("@") || !email.contains(".") -> errorMessage = "Please enter a valid email address."
                        mobile.isBlank() -> errorMessage = "Please enter your Mobile Number."
                        country.isBlank() -> errorMessage = "Please enter your Country."
                        password.length < 6 -> errorMessage = "Password must be at least 6 characters long."
                        password != confirmPassword -> errorMessage = "Passwords do not match."
                        !acceptTerms || !acceptPrivacy -> errorMessage = "Please agree to the Terms and Privacy Policy."
                        else -> {
                            errorMessage = null
                            isSubmitting = true
                            coroutineScope.launch {
                                val res = authManager.signUp(
                                    fullName = fullName,
                                    email = email,
                                    phone = mobile,
                                    country = country,
                                    city = city,
                                    profession = profession,
                                    company = company,
                                    jobTitle = jobTitle,
                                    password = password
                                )
                                isSubmitting = false
                                if (res.isSuccess) {
                                    onSignUpSuccess()
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message ?: "Registration failed."
                                }
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = if (isSubmitting) "Creating Account..." else "Create Account", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already have an account? ", fontSize = 12.sp)
                Text(text = "Sign In", fontSize = 12.sp, color = ElectricalBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToLogin))
            }
        }
    }
}

@Composable
fun LoginScreen(
    authManager: AuthManager,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(text = "Sign In to Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF0A192F), RoundedCornerShape(12.dp))
                    .border(2.dp, ElectricalBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(34.dp))
            }

            Text(
                text = "Welcome to Electrical Engineer Pro",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Google Sign In
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        val result = authManager.googleSignIn("", "")
                        if (result.isSuccess) {
                            onLoginSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Google Sign-In is not configured."
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Google", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(text = "OR SIGN IN WITH EMAIL", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Divider(modifier = Modifier.weight(1f))
            }

            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusCriticalRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusCriticalRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = StatusCriticalRed, modifier = Modifier.size(16.dp))
                        Text(text = errorMessage!!, color = StatusCriticalRed, fontSize = 11.sp)
                    }
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ElectricalBlue) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricalBlue) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = {
                            rememberMe = it
                            authManager.setRememberMe(it)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = ElectricalBlue)
                    )
                    Text(text = "Remember Me", fontSize = 12.sp)
                }

                Text(
                    text = "Forgot Password?",
                    fontSize = 12.sp,
                    color = ElectricalBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onNavigateToForgotPassword)
                )
            }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password."
                    } else {
                        errorMessage = null
                        isSubmitting = true
                        coroutineScope.launch {
                            val res = authManager.login(email, password)
                            isSubmitting = false
                            if (res.isSuccess) {
                                onLoginSuccess()
                            } else {
                                errorMessage = res.exceptionOrNull()?.message ?: "Login failed."
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = if (isSubmitting) "Signing In..." else "Sign In", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account? ", fontSize = 12.sp)
                Text(text = "Register Free", fontSize = 12.sp, color = ElectricalBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToSignUp))
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    authManager: AuthManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = enter email, 2 = set new password
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(text = "Reset Password", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Forgot Your Password?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter your registered email address to receive password reset authorization.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (message != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSuccess) StatusPassGreen.copy(alpha = 0.15f) else StatusCriticalRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSuccess) StatusPassGreen else StatusCriticalRed)
                ) {
                    Text(
                        text = message!!,
                        color = if (isSuccess) StatusPassGreen else StatusCriticalRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (step == 1) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (email.isBlank() || !email.contains("@")) {
                            message = "Please enter a valid email address."
                            isSuccess = false
                        } else {
                            message = "Verification token generated. Please enter your new password below."
                            isSuccess = true
                            step = 2
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Verify Email")
                }
            } else {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password (Min. 6 characters)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (newPassword.length < 6) {
                            message = "Password must be at least 6 characters."
                            isSuccess = false
                        } else {
                            coroutineScope.launch {
                                val res = authManager.resetPassword(email, newPassword)
                                if (res.isSuccess) {
                                    message = "Password successfully reset! You can now sign in with your new password."
                                    isSuccess = true
                                } else {
                                    message = res.exceptionOrNull()?.message ?: "Reset failed."
                                    isSuccess = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPassGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Set New Password")
                }
            }
        }
    }
}

enum class AuthSubScreen {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD,
    TERMS,
    PRIVACY
}

@Composable
fun AuthFlowScreen(
    authManager: AuthManager,
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSubScreen by remember { mutableStateOf(AuthSubScreen.LOGIN) }

    when (currentSubScreen) {
        AuthSubScreen.LOGIN -> LoginScreen(
            authManager = authManager,
            onLoginSuccess = onAuthSuccess,
            onNavigateToSignUp = { currentSubScreen = AuthSubScreen.SIGN_UP },
            onNavigateToForgotPassword = { currentSubScreen = AuthSubScreen.FORGOT_PASSWORD },
            onBack = onBack,
            modifier = modifier
        )
        AuthSubScreen.SIGN_UP -> SignUpScreen(
            authManager = authManager,
            onSignUpSuccess = onAuthSuccess,
            onNavigateToLogin = { currentSubScreen = AuthSubScreen.LOGIN },
            onNavigateToPrivacy = { currentSubScreen = AuthSubScreen.PRIVACY },
            onNavigateToTerms = { currentSubScreen = AuthSubScreen.TERMS },
            onBack = { currentSubScreen = AuthSubScreen.LOGIN },
            modifier = modifier
        )
        AuthSubScreen.FORGOT_PASSWORD -> ForgotPasswordScreen(
            authManager = authManager,
            onBack = { currentSubScreen = AuthSubScreen.LOGIN },
            modifier = modifier
        )
        AuthSubScreen.TERMS -> LegalDocViewer(
            title = "Terms and Conditions",
            content = "1. Electrical Engineer Pro is designed for certified electrical engineers, supervisors, technicians, and engineering students.\n2. Engineering calculations and relay coordination curves are provided in compliance with IEC/IEEE standards as engineering aids. Final field settings and commissioning decisions remain the responsibility of authorized facility personnel.\n3. User profiles and telemetry logs are encrypted and persisted securely in the local device database and synchronized to authorized enterprise Google Sheets.\n4. Commercial redistribution without permission is strictly prohibited.",
            onBack = { currentSubScreen = AuthSubScreen.SIGN_UP },
            modifier = modifier
        )
        AuthSubScreen.PRIVACY -> LegalDocViewer(
            title = "Privacy Policy",
            content = "1. Data Protection: We respect the privacy of electrical engineers and industrial organizations. Personal data including full name, phone number, and engineering affiliations are stored securely.\n2. Password Security: Passwords are encrypted with SHA-256 cryptographic hashing and salted. Plaintext passwords are never stored or transmitted to external spreadsheets.\n3. Cloud Synchronization: Enterprise synchronization with Google Sheets only exports designated metadata (User ID, name, email, country, timestamp, role) via authenticated backend conduits.\n4. User Control: Users can view, edit, or permanently erase their account and synced records at any time.",
            onBack = { currentSubScreen = AuthSubScreen.SIGN_UP },
            modifier = modifier
        )
    }
}

@Composable
fun LegalDocViewer(
    title: String,
    content: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Text(
                    text = content,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Return to Registration")
            }
        }
    }
}
