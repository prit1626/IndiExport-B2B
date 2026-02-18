import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import chatApi from '../../api/chatApi';
import ChatShell from '../../components/chat/ChatShell';
import ChatList from '../../components/chat/ChatList';
import { MessageSquare } from 'lucide-react';
import toast from 'react-hot-toast';

const SellerInquiriesPage = () => {
    const navigate = useNavigate();
    const [threads, setThreads] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchThreads = async () => {
            try {
                setLoading(true);
                const { data } = await chatApi.getSellerInquiryThreads();
                setThreads(data);
            } catch (error) {
                console.error(error);
                toast.error('Failed to load inquiries');
            } finally {
                setLoading(false);
            }
        };
        fetchThreads();
    }, []);

    const EmptyState = (
        <div className="flex-1 flex flex-col items-center justify-center p-8 text-center bg-slate-50">
            <div className="p-4 bg-white rounded-full shadow-sm mb-4">
                <MessageSquare size={48} className="text-slate-300" />
            </div>
            <h3 className="text-lg font-semibold text-slate-900 mb-2">Buyer Inquiries</h3>
            <p className="text-slate-500 max-w-xs">
                Select an inquiry to respond to potential buyers.
            </p>
        </div>
    );

    return (
        <ChatShell
            isMobileChatOpen={false}
            sidebar={
                <>
                    <div className="p-4 border-b border-slate-200 bg-white">
                        <h1 className="text-xl font-bold text-slate-800">Inquiries</h1>
                    </div>
                    <ChatList
                        threads={threads}
                        loading={loading}
                        onSelect={(chatId) => navigate(`/seller/inquiries/${chatId}`)}
                    />
                </>
            }
            content={EmptyState}
        />
    );
};

export default SellerInquiriesPage;
