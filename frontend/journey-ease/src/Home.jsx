import React from 'react';
import { Typography, Box } from '@mui/material';

const Home = () => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      justifyContent="center"
      alignItems="center"
      height="100vh"
    >
      <Typography variant="h2" component="h1" align="center" sx={{ color: '#1b4965' }}>
        HAKUNA MATATA
      </Typography>
      <Typography variant="h6" component="h2" align="center" sx={{ marginTop: 2 }}>
        Route-Annotation Program
      </Typography>
    </Box>
  );
};

export default Home;