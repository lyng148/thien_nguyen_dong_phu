import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Grid,
  TextField,
  Button,
  FormControlLabel,
  Switch,
  Alert,
  CircularProgress,
  Divider
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as ArrowBackIcon } from '@mui/icons-material';

import PageHeader from '../common/PageHeader';
import { getHouseholdById, createHousehold, updateHousehold } from '../../services/householdService';

const HouseholdForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  
  // Form state
  const [formData, setFormData] = useState({
    ownerName: '',
    address: '',
    numMembers: 1,
    phoneNumber: '',
    email: '',
    active: true
  });
  
  // UI state
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Load household data if editing
  useEffect(() => {
    const fetchHousehold = async () => {
      if (isEdit) {
        try {
          setLoading(true);
          console.log('Fetching household with ID:', id);
          const data = await getHouseholdById(id);
          console.log('Received household data:', data);
          setFormData({
            ownerName: data.ownerName || '',
            address: data.address || '',
            numMembers: data.numMembers || 1,
            phoneNumber: data.phoneNumber || '',
            email: data.email || '',
            active: data.active !== undefined ? data.active : true
          });
        } catch (err) {
          console.error('Error fetching household:', err);
          setError('Error loading household data. Please try again.');
        } finally {
          setLoading(false);
        }
      }
    };
    
    fetchHousehold();
  }, [isEdit, id]);

  // Handle form changes
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  // Handle number input
  const handleNumberChange = (e) => {
    const { name, value } = e.target;
    if (value === '' || parseInt(value) > 0) {
      setFormData(prev => ({
        ...prev,
        [name]: value === '' ? '' : parseInt(value)
      }));
    }
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSaving(true);
    
    try {
      // Make sure numMembers is an integer
      const householdData = {
        ...formData,
        numMembers: parseInt(formData.numMembers, 10)
      };
      
      console.log('Submitting household data:', householdData);
      
      let result;
      if (isEdit) {
        console.log(`Updating household with ID ${id}`);
        result = await updateHousehold(id, householdData);
        console.log('Household updated successfully:', result);
        setSuccess('Household updated successfully');
      } else {
        console.log('Creating new household');
        result = await createHousehold(householdData);
        console.log('Household created successfully:', result);
        setSuccess('Household created successfully');
      }
      
      // Redirect after successful save
      setTimeout(() => {
        console.log('Redirecting to households list');
        navigate('/households');
      }, 1500);
      
    } catch (err) {
      console.error('Save household error:', err);
      console.error('Error details:', {
        message: err.message,
        response: err.response?.data,
        status: err.response?.status
      });
      setError(err.response?.data?.message || err.message || 'An error occurred while saving the household');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box>
      <PageHeader 
        title={isEdit ? 'Edit Household' : 'Add Household'} 
        subtitle={isEdit ? 'Update household information' : 'Create a new household record'}
        breadcrumbs={[
          { label: 'Dashboard', path: '/dashboard' },
          { label: 'Households', path: '/households' },
          { label: isEdit ? 'Edit' : 'Add' }
        ]}
      />

      <Card>
        <CardContent>
          {loading ? (
            <Box display="flex" justifyContent="center" my={4}>
              <CircularProgress />
            </Box>
          ) : (
            <form onSubmit={handleSubmit}>
              {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                  {error}
                </Alert>
              )}
              
              {success && (
                <Alert severity="success" sx={{ mb: 3 }}>
                  {success}
                </Alert>
              )}

              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Owner Name"
                    name="ownerName"
                    value={formData.ownerName}
                    onChange={handleChange}
                    fullWidth
                    required
                    disabled={saving}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Number of Members"
                    name="numMembers"
                    type="number"
                    value={formData.numMembers}
                    onChange={handleNumberChange}
                    fullWidth
                    required
                    inputProps={{ min: 1 }}
                    disabled={saving}
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <TextField
                    label="Address"
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    fullWidth
                    required
                    disabled={saving}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Phone Number"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleChange}
                    fullWidth
                    disabled={saving}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Email"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    fullWidth
                    disabled={saving}
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        name="active"
                        checked={formData.active}
                        onChange={handleChange}
                        disabled={saving}
                        color="success"
                      />
                    }
                    label="Active"
                  />
                </Grid>
              </Grid>
              
              <Divider sx={{ my: 3 }} />
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/households')}
                  startIcon={<ArrowBackIcon />}
                  disabled={saving}
                >
                  Cancel
                </Button>
                
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={<SaveIcon />}
                  disabled={saving}
                >
                  {saving ? (
                    <>
                      <CircularProgress size={20} sx={{ mr: 1 }} />
                      Saving...
                    </>
                  ) : (
                    'Save Household'
                  )}
                </Button>
              </Box>
            </form>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default HouseholdForm; 