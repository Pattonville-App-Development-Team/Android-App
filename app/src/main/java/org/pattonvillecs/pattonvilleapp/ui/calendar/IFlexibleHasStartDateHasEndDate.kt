package org.pattonvillecs.pattonvilleapp.ui.calendar

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.items.IFlexible
import org.pattonvillecs.pattonvilleapp.model.calendar.event.HasEndDate
import org.pattonvillecs.pattonvilleapp.model.calendar.event.HasStartDate

/**
 * This interface serves to provide a concrete type of something that implements [IFlexible], [HasStartDate], and [HasEndDate]
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
interface IFlexibleHasStartDateHasEndDate<VH : RecyclerView.ViewHolder> : IFlexible<VH>, HasStartDate, HasEndDate