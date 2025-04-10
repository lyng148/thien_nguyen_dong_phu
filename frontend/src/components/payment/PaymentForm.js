import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Grid,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  FormControlLabel,
  Switch,
  Alert,
  CircularProgress,
  Divider,
  InputAdornment,
  Autocomplete
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as ArrowBackIcon } from '@mui/icons-material';

import PageHeader from '../common/PageHeader';
import { getPaymentById, createPayment, updatePayment } from '../../services/paymentService';
import { getAllHouseholds } from '../../services/householdService';
import { getAllFees } from '../../services/feeService';

const PaymentForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  
  // Form state
  const [formData, setFormData] = useState({
    householdId: '',
    household: null,
    feeId: '',
    fee: null,
    amount: '',
    amountPaid: '',
    paymentDate: formatDateForInput(new Date()),
    verified: false,
    notes: ''
  });
  
  // Data state
  const [households, setHouseholds] = useState([]);
  const [fees, setFees] = useState([]);
  
  // UI state
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Load payment data if editing
  useEffect(() => {
    const loadData = async () => {
      try {
        // Load households and fees from API
        const [householdsData, feesData] = await Promise.all([
          getAllHouseholds(),
          getAllFees()
        ]);
        
        setHouseholds(householdsData);
        setFees(feesData);
        
        // If editing, load payment data from API
        if (isEdit) {
          const paymentData = await getPaymentById(id);
          console.log('Received payment data (DTO format):', paymentData);
          
          // Find the actual household and fee objects from the loaded data
          const selectedHousehold = householdsData.find(h => h.id === paymentData.householdId);
          const selectedFee = feesData.find(f => f.id === paymentData.feeId);
          
          console.log('Edit mode - found household:', selectedHousehold);
          console.log('Edit mode - found fee:', selectedFee);
          
          // Format data for form
          setFormData({
            ...paymentData,
            householdId: paymentData.householdId || '',
            household: selectedHousehold || null,
            feeId: paymentData.feeId || '',
            fee: selectedFee || null,
            amount: paymentData.amount || 0,
            amountPaid: paymentData.amountPaid || paymentData.amount || 0,
            paymentDate: formatDateForInput(paymentData.paymentDate)
          });
        }
      } catch (err) {
        console.error('Load data error:', err);
        setError('Failed to load data. Please try again.');
      } finally {
        setLoading(false);
      }
    };
    
    loadData();
  }, [isEdit, id]);

  // Handle form changes
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  // Handle household selection
  const handleHouseholdChange = (event, newValue) => {
    setFormData(prev => ({
      ...prev,
      household: newValue,
      householdId: newValue ? newValue.id : ''
    }));
    console.log('Selected household:', newValue?.id);
  };

  // Handle fee selection
  const handleFeeChange = (event, newValue) => {
    setFormData(prev => ({
      ...prev,
      fee: newValue,
      feeId: newValue ? newValue.id : '',
      amount: newValue ? newValue.amount : '',
      amountPaid: newValue ? newValue.amount : ''
    }));
    console.log('Selected fee:', newValue?.id);
  };

  // Handle number input
  const handleNumberChange = (e) => {
    const { name, value } = e.target;
    if (value === '' || parseFloat(value) >= 0) {
      setFormData(prev => ({
        ...prev,
        [name]: value === '' ? '' : parseFloat(value)
      }));
    }
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    
    // Validate form data
    if (!formData.householdId) {
      setError('Please select a household');
      return;
    }
    
    if (!formData.feeId) {
      setError('Please select a fee');
      return;
    }
    
    if (!formData.amount || formData.amount <= 0) {
      setError('Please enter a valid payment amount');
      return;
    }
    
    setSaving(true);
    
    try {
      // Format data for API
      const paymentData = {
        householdId: parseInt(formData.householdId, 10),
        feeId: parseInt(formData.feeId, 10),
        amount: parseFloat(formData.amount),
        amountPaid: parseFloat(formData.amountPaid || formData.amount),
        paymentDate: formData.paymentDate,
        verified: formData.verified,
        notes: formData.notes || ''
      };
      
      console.log('Submitting payment data:', paymentData);
      
      if (isEdit) {
        await updatePayment(id, paymentData);
        setSuccess('Payment updated successfully');
      } else {
        await createPayment(paymentData);
        setSuccess('Payment created successfully');
      }
      
      // Redirect after successful save
      setTimeout(() => {
        navigate('/payments');
      }, 1500);
      
    } catch (err) {
      console.error('Save payment error:', err);
      setError(err.response?.data?.message || 'An error occurred while saving the payment');
    } finally {
      setSaving(false);
    }
  };

  // Format date for input
  function formatDateForInput(dateValue) {
    if (!dateValue) return '';
    const date = new Date(dateValue);
    return date.toISOString().split('T')[0];
  }

  return (
    <Box>
      <PageHeader 
        title={isEdit ? 'Edit Payment' : 'Add Payment'} 
        subtitle={isEdit ? 'Update payment information' : 'Create a new payment record'}
        breadcrumbs={[
          { label: 'Dashboard', path: '/dashboard' },
          { label: 'Payments', path: '/payments' },
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
                  <Autocomplete
                    options={households}
                    getOptionLabel={(option) => `${option.ownerName} (${option.address})`}
                    value={formData.household}
                    onChange={handleHouseholdChange}
                    isOptionEqualToValue={(option, value) => option.id === value.id}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        label="Household"
                        required
                        error={!formData.householdId && formData.householdId !== ''}
                        helperText={!formData.householdId && 'Household is required'}
                        disabled={saving}
                      />
                    )}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Autocomplete
                    options={fees}
                    getOptionLabel={(option) => `${option.name} ($${option.amount})`}
                    value={formData.fee}
                    onChange={handleFeeChange}
                    isOptionEqualToValue={(option, value) => option.id === value.id}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        label="Fee"
                        required
                        error={!formData.feeId && formData.feeId !== ''}
                        helperText={!formData.feeId && 'Fee is required'}
                        disabled={saving}
                      />
                    )}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Amount"
                    name="amount"
                    type="number"
                    value={formData.amount}
                    onChange={handleNumberChange}
                    fullWidth
                    required
                    disabled={saving}
                    InputProps={{
                      startAdornment: <InputAdornment position="start">$</InputAdornment>,
                    }}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Amount Paid"
                    name="amountPaid"
                    type="number"
                    value={formData.amountPaid}
                    onChange={handleNumberChange}
                    fullWidth
                    required
                    disabled={saving}
                    InputProps={{
                      startAdornment: <InputAdornment position="start">$</InputAdornment>,
                    }}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Payment Date"
                    name="paymentDate"
                    type="date"
                    value={formatDateForInput(formData.paymentDate)}
                    onChange={handleChange}
                    fullWidth
                    required
                    disabled={saving}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <TextField
                    label="Notes"
                    name="notes"
                    value={formData.notes}
                    onChange={handleChange}
                    fullWidth
                    multiline
                    rows={3}
                    disabled={saving}
                    placeholder="Optional notes about this payment"
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        name="verified"
                        checked={formData.verified}
                        onChange={handleChange}
                        disabled={saving}
                        color="success"
                      />
                    }
                    label="Verified"
                  />
                </Grid>
              </Grid>
              
              <Divider sx={{ my: 3 }} />
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/payments')}
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
                      <CircularProgress size={24} sx={{ mr: 1 }} color="inherit" />
                      Saving...
                    </>
                  ) : (
                    isEdit ? 'Update Payment' : 'Create Payment'
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

export default PaymentForm; 