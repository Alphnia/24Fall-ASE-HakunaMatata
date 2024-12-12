import React, { useRef, useState, useEffect } from 'react';
import SignUpForm from './SignUpForm';

export default function SignUp(props) {
  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'centre',
        alignItems: 'centre',
        height: '100vh'
      }}
    >
      <SignUpForm />
    </div>
  );
}