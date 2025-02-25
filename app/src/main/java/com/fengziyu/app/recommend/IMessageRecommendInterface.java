package com.fengziyu.app.recommend;

import android.os.Binder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.fengziyu.app.model.Message;
import java.util.List;

public interface IMessageRecommendInterface extends IInterface {
    static final String DESCRIPTOR = "com.fengziyu.app.recommend.IMessageRecommendInterface";

    List<Message> getRecommendedMessages(int count) throws RemoteException;

    public static abstract class Stub extends Binder implements IMessageRecommendInterface {
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IMessageRecommendInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IMessageRecommendInterface))) {
                return ((IMessageRecommendInterface) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getRecommendedMessages: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    List<Message> _result = this.getRecommendedMessages(_arg0);
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IMessageRecommendInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            @Override
            public List<Message> getRecommendedMessages(int count) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                List<Message> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(count);
                    mRemote.transact(TRANSACTION_getRecommendedMessages, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(Message.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_getRecommendedMessages = (android.os.IBinder.FIRST_CALL_TRANSACTION);
    }
} 