package pt.ipca.roomies.ui.main

class LandlordFragment: Fragment{

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usersRecyclerView: RecyclerView = view.findViewById(R.id.users_recycler_view)
        usersRecyclerView.adapter = UserAdapter(/* your user data */)
        val itemTouchHelper = ItemTouchHelper(SwipeCallback(usersRecyclerView.adapter!!))
        itemTouchHelper.attachToRecyclerView(usersRecyclerView)
    }
}