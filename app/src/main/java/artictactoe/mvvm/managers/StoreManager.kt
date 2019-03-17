package artictactoe.mvvm.managers

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*


/**
 * Created by Iván Carrasco Alonso on 10/12/2018.
 */
/** Helper class for Firebase storage of cloud anchor IDs. */
class StoreManager(context: Context) {
    /** Listener for a new Cloud Anchor ID from the Firebase Database.  */
    interface CloudAnchorIdListener {
        fun onCloudAnchorIdAvailable(cloudAnchorId: String?)
    }

    /** Listener for a new short code from the Firebase Database.  */
    interface ShortCodeListener {
        fun onShortCodeAvailable(shortCode: Int?)
    }

    private lateinit var rootRef: DatabaseReference

    init {
        val fireBaseApp = FirebaseApp.initializeApp(context)
        fireBaseApp?.let {
            rootRef = FirebaseDatabase.getInstance(fireBaseApp).reference.child(KEY_ROOT_DIR)
            DatabaseReference.goOnline()
        }
    }

    /** Gets a new short code that can be used to store the anchor ID. */
    fun nextShortCode(listener: ShortCodeListener) {
        // Run a transaction on the node containing the next short code available. This increments the
        // value in the database and retrieves it in one atomic all-or-nothing operation.
        rootRef
            .child(KEY_NEXT_SHORT_CODE)
            .runTransaction(
                object : Transaction.Handler {
                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        if (!p1) {
                            Log.e(TAG, "Firebase Error", p0?.toException())
                            listener.onShortCodeAvailable(null)
                        } else {
                            p2?.value?.let {
                                val shortCode: Int = (it as Long).toInt()
                                listener.onShortCodeAvailable(shortCode)
                            }
                        }
                    }

                    override fun doTransaction(p0: MutableData): Transaction.Result {
                        var shortCode: Int? = (p0.value as? Long)?.toInt()
                        if (shortCode == null) {
                            shortCode = INITIAL_SHORT_CODE - 1
                        }
                        p0.value = shortCode + 1
                        return Transaction.success(p0)
                    }
                }
            )
    }

    /** Stores the cloud anchor ID in the configured Firebase Database.  */
    fun storeUsingShortCode(shortCode: Int, cloudAnchorId: String) {
        rootRef.child(KEY_PREFIX + shortCode).setValue(cloudAnchorId)
    }

    /**
     * Retrieves the cloud anchor ID using a short code. Returns an empty string if a cloud anchor ID
     * was not stored for this short code.
     */
    fun getCloudAnchorID(shortCode: Int, listener: CloudAnchorIdListener) {
        rootRef
            .child(KEY_PREFIX + shortCode)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        listener.onCloudAnchorIdAvailable(dataSnapshot.value.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            TAG, "The database operation for getCloudAnchorID was cancelled.",
                            error.toException()
                        )
                        listener.onCloudAnchorIdAvailable(null)
                    }
                })
    }

    fun updateCloudAnchorID(shortCode: Int, cloudAnchorId: String) {
        rootRef
            .child(KEY_PREFIX + shortCode)
            .setValue(cloudAnchorId)
    }

    companion object {
        val TAG = StoreManager::class.java.name
        const val KEY_ROOT_DIR = "ar_tic_tac_toe"
        const val KEY_NEXT_SHORT_CODE = "next_short_code"
        const val KEY_PREFIX = "anchor;"
        const val INITIAL_SHORT_CODE = 0
    }
}