
import React, { useState, useEffect } from 'react';
import { Settings, ShieldCheck } from 'lucide-react';
import { toast } from 'react-hot-toast';
import adminApi from '../../api/adminApi';
import SettingsForm from '../../components/adminSettings/SettingsForm';
import SettingsSkeleton from '../../components/adminSettings/SettingsSkeleton';
import SettingsErrorState from '../../components/adminSettings/SettingsErrorState';

const AdminSettingsPage = () => {
    const [settings, setSettings] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [saving, setSaving] = useState(false);

    const fetchSettings = async () => {
        setLoading(true);
        setError(false);
        try {
            const { data } = await adminApi.getAdminSettings();
            setSettings(data);
        } catch (err) {
            console.error(err);
            setError(true);
            toast.error('Failed to load settings');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSettings();
    }, []);

    const handleSave = async (updatedData) => {
        setSaving(true);
        try {
            const { data } = await adminApi.updateAdminSettings(updatedData);
            setSettings(data);
            toast.success('Settings updated successfully');
        } catch (err) {
            console.error(err);
            toast.error('Failed to update settings');
        } finally {
            setSaving(false);
        }
    };

    if (loading) return (
        <div className="min-h-screen bg-slate-50 p-6">
            <div className="container mx-auto max-w-4xl">
                <SettingsSkeleton />
            </div>
        </div>
    );

    if (error) return (
        <div className="min-h-screen bg-slate-50 p-6">
            <div className="container mx-auto max-w-4xl">
                <SettingsErrorState onRetry={fetchSettings} />
            </div>
        </div>
    );

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6 mb-8">
                <div className="container mx-auto px-6 max-w-4xl">
                    <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
                        <Settings className="text-slate-600" /> Platform Configuration
                    </h1>
                    <p className="text-slate-500 mt-1">Manage global marketplace settings, fees, and rules.</p>
                </div>
            </div>

            <div className="container mx-auto px-6 max-w-4xl">
                <SettingsForm
                    initialData={settings}
                    onSave={handleSave}
                    isSaving={saving}
                />
            </div>
        </div>
    );
};

export default AdminSettingsPage;
