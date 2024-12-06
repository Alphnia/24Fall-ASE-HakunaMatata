import React, { useState, useEffect, useMemo } from 'react';
import Button from '@mui/material/Button';
import SendIcon from '@mui/icons-material/Send';

const ShareLocaion = () => {
// function RouteDisplay(props) {
  return (
    <div>
      <Button variant="contained" endIcon={<SendIcon />}>
        Send
      </Button>
    </div>
  );
};


export default ShareLocaion;