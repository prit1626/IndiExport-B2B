import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/navigation/Sidebar';
import Topbar from '../components/navigation/Topbar';
import MobileDrawer from '../components/navigation/MobileDrawer';

const SellerLayout = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    return (
        <div className="flex min-h-screen bg-slate-50 dark:bg-slate-950">
            {/* Desktop Sidebar */}
            <Sidebar role="SELLER" />

            {/* Mobile Sidebar / Drawer */}
            <MobileDrawer
                role="SELLER"
                isOpen={isMobileMenuOpen}
                onClose={() => setIsMobileMenuOpen(false)}
            />

            <div className="flex-1 flex flex-col min-w-0">
                <Topbar onMenuClick={() => setIsMobileMenuOpen(true)} />

                <main className="flex-1 overflow-x-hidden p-4 md:p-8">
                    <div className="max-w-7xl mx-auto h-full">
                        <Outlet />
                    </div>
                </main>
            </div>
        </div>
    );
};

export default SellerLayout;
