import React, { useState } from 'react';
import { TextField, Button, Typography, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';

function SignUpForm() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const validateForm = () => {
        const newErrors = {};
        if (!formData.name) newErrors.name = 'Name is required';
        if (!formData.email) newErrors.email = 'Email is required';
        else if (!/\S+@\S+\.\S+/.test(formData.email))
            newErrors.email = 'Email is not valid';
        if (!formData.password) newErrors.password = 'Password is required';
        else if (formData.password.length < 6)
            newErrors.password = 'Password must be at least 6 characters long';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validateForm()) {
            console.log('Form Submitted', formData);
            const formDataJson = JSON.stringify(formData);

            fetch("http://localhost:8080"+"/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: formDataJson,
            })
                .then((res) => {
                    if (!res.ok) {
                        return res.json().then((error) => {
                            throw new Error(
                                error.message || `HTTP error! Status: ${res.status}`
                            );
                        });
                    }
                    return res.json();
                })
                .then((data) => {
                    console.log("Registration successful:", data);

                    // 保存用户 ID 到 localStorage
                    localStorage.setItem("userId", data.id);

                    // 跳转到登录页面
                    navigate("/LogIn");
                })
                .catch((error) => {
                    console.error("Error:", error.message);
                    setErrors({ server: error.message });
                });
        }
    };

    return (
        <Box
            component="form"
            onSubmit={handleSubmit}
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                maxWidth: 400,
                margin: 'auto',
                p: 3,
                border: '1px solid #ddd',
                borderRadius: 2,
            }}
        >
            <Typography variant="h4" gutterBottom>
                Sign Up
            </Typography>

            <TextField
                label="Name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                error={!!errors.name}
                helperText={errors.name}
                margin="normal"
                fullWidth
                required
            />

            <TextField
                label="Email"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                error={!!errors.email}
                helperText={errors.email}
                margin="normal"
                fullWidth
                required
            />

            <TextField
                label="Password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                error={!!errors.password}
                helperText={errors.password}
                margin="normal"
                fullWidth
                required
            />

            {errors.server && (
                <Typography color="error" variant="body2">
                    {errors.server}
                </Typography>
            )}

            <Button
                type="submit"
                variant="contained"
                color="primary"
                sx={{ mt: 2 }}
                fullWidth
            >
                Sign Up
            </Button>
        </Box>
    );
}

export default SignUpForm;
