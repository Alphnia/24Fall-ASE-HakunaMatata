import React, { useState, useEffect, useMemo } from 'react';
import { useLocation } from 'react-router-dom';
import { Stepper, Step, StepLabel, Typography, Box, nativeSelectClasses } from '@mui/material';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';

const Annotations = () => {
  const location = useLocation();
  const [annotations, setAnnotations] = useState("turn left");
  const {trainData, stopDatajson} = location.state || {};
  console.log(stopDatajson);
  const handleChange = (e) => {
    setAnnotations(e.target.value);
    console.log(e.target.value);
  }

  const handleSaveAnno = () => {
   
  }

  return (
    <div>
      
      { 
        trainData && (
            <div>
              {trainData.map((stops, trainIndex) => (
                  <Box key={trainIndex} sx={{ marginBottom: 4 }}>
                    <Typography variant="h6" style={{ fontSize: '1.5em', color: '#1b4965' }}>
                      Train {trainIndex + 1} Stop Details:
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', marginTop: 4 }}> 
                      <Stepper activeStep={0} orientation="vertical">
                        {['arrivalStop', 'departureStop'].reverse().map((key, index) => (
                          stops[key] && (
                            <Step key={index} sx={{ '& .MuiStepLabel-root': { color: '#bee9e8' } }}>
                              <StepLabel sx={{
                                '&.Mui-active': { color: '#009688' },
                                '&.Mui-completed': { color: '#4CAF50' }
                              }}>
                                {stops[key]}
                              </StepLabel>
                              <TextField
                                id="standard-textarea"
                                label="Add your notes here:"
                                placeholder="Placeholder"
                                multiline
                                variant="standard"
                                value={annotations}
                                onChange={handleChange}
                                />
                            </Step>
                          )
                        ))}
                      </Stepper>
                    </Box>
                  </Box>
                ))}
              <Button 
                variant="contained" 
                sx={{ marginLeft: 2, marginTop: 4, textTransform: 'none', backgroundColor: '#bee9e8',color: '#1b4965' }}
                onClick={handleSaveAnno}
              >
                Save Annotations
              </Button>
            </div>
          
      )}
    </div>
  );
};


export default Annotations;