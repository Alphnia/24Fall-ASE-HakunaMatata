import React, { useState, useEffect, useMemo } from 'react';
import { useLocation } from 'react-router-dom';
import { Stepper, Step, StepLabel, Typography, Box, nativeSelectClasses } from '@mui/material';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
const userId = localStorage.getItem('userid');

const Annotations = () => {
  const location = useLocation();
  const { trainData, stopDatajson, annoList, routeId} = location.state || {};
  const [annotations, setAnnotations] = useState(() => {
      if (annoList.length === 0) {
          return trainData.map((data) => {
              return Object.keys(data).reduce((acc, key) => {
                  acc[key] = typeof data[key] === 'object' && !Array.isArray(data[key]) ? {} : ""; // Handle nested objects
                  return acc;
              }, {});
          });
      }
      return annoList;
  });

  const handleChange = (e, trainIndex, key) => {
    setAnnotations((prevAnnotations) => {
      const updatedAnnotations = [...prevAnnotations];
      updatedAnnotations[trainIndex] = {
        ...updatedAnnotations[trainIndex],
        [key]: e.target.value
      };
      return updatedAnnotations;
    });
  
    console.log(`Updated value: ${e.target.value}, TrainIndex: ${trainIndex}, Key: ${key}`);
  };

  const handleSaveAnno = async () => {
    try {
      const url = "http://localhost:8080/editRoute";

      // Make the PATCH request
      const response = await fetch(url, {
          method: "PATCH",
          headers: {
              "Content-Type": "application/json",
          },
          body: JSON.stringify({
              routeId,
              userId,
              annotations,
          }),
      });

      // Handle the response
      if (response.ok) {
          const result = await response.json();
          console.log("Annotation saved successfully:", result);
      } else {
          const errorText = await response.text();
          console.error("Failed to save annotation:", errorText);
      }
  } catch (error) {
      console.error("Error while saving annotation:", error);
  }
  }

  return (
    <div>
      
      { 
        trainData && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: 5 }}>
            
              {trainData.map((stops, trainIndex) => (
                  <Box key={trainIndex} sx={{ marginBottom: 4 }}>
                    <Typography variant="h6" style={{ fontSize: '1.5em', color: '#1b4965' }}>
                      Train {trainIndex + 1} Stop Details:
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', marginTop: 0 }}> 
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
                                value={annotations[trainIndex][key]}
                                onChange={(e) => handleChange(e, trainIndex, key)}
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
              </Box>
            
          
      )}
    </div>
  );
};


export default Annotations;