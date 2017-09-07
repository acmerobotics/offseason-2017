package com.acmerobotics.library.dashboard;

import android.support.annotation.Nullable;

import com.acmerobotics.library.dashboard.message.Message;
import com.acmerobotics.library.dashboard.message.MessageType;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan
 */

public class DashboardTelemetry implements Telemetry {
    public class LogImpl implements Log {
        private transient int capacity = 9;
        private DisplayOrder displayOrder;
        private List<String> lines;

        public LogImpl() {
            this.lines = new ArrayList<>();
        }

        @Override
        public int getCapacity() {
            return this.capacity;
        }

        @Override
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public DisplayOrder getDisplayOrder() {
            return this.displayOrder;
        }

        @Override
        public void setDisplayOrder(DisplayOrder displayOrder) {
            this.displayOrder = displayOrder;
        }

        @Override
        public void add(String line) {
            this.lines.add(line);
        }

        @Override
        public void add(String line, Object... objects) {
            this.add(String.format(line, objects));
        }

        @Override
        public void clear() {
            this.lines.clear();
        }
    }

    public class LineImpl implements Line {
        private List<ItemImpl> items;

        public LineImpl() {
            this.items = new ArrayList<>();
        }

        @Override
        public Item addData(String caption, String value, Object... objects) {
            ItemImpl item = new ItemImpl(this);
            item.setCaption(caption);
            item.setValue(value, objects);
            this.items.add(item);
            return item;
        }

        @Override
        public Item addData(String caption, Object value) {
            ItemImpl item = new ItemImpl(this);
            item.setCaption(caption);
            item.setValue(value);
            this.items.add(item);
            return item;
        }

        @Override
        public <T> Item addData(String caption, Func<T> func) {
            ItemImpl item = new ItemImpl(this);
            item.setCaption(caption);
            item.setValue(func);
            this.items.add(item);
            return item;
        }

        @Override
        public <T> Item addData(String caption, String value, Func<T> func) {
            ItemImpl item = new ItemImpl(this);
            item.setCaption(caption);
            item.setValue(value, value, func);
            this.items.add(item);
            return item;
        }
    }

    public class ItemImpl implements Item {
        private transient Line parent;

        private String caption, value;

        public ItemImpl(Line parent) {
            this.parent = parent;
        }

        @Override
        public String getCaption() {
            return this.caption;
        }

        @Override
        public Item setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        @Override
        public Item setValue(String value, Object... objects) {
            return setValue(String.format(value, objects));
        }

        @Override
        public Item setValue(Object value) {
            this.value = value.toString();
            return this;
        }

        @Override
        public <T> Item setValue(final Func<T> func) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Item setValue(final String caption, final Func<T> func) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item setRetained(@Nullable Boolean aBoolean) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isRetained() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item addData(String caption, String value, Object... objects) {
            this.parent.addData(caption, value, objects);
            return this;
        }

        @Override
        public Item addData(String caption, Object value) {
            this.parent.addData(caption, value);
            return this;
        }

        @Override
        public <T> Item addData(String caption, Func<T> func) {
            this.parent.addData(caption, func);
            return this;
        }

        @Override
        public <T> Item addData(String caption, String value, Func<T> func) {
            this.parent.addData(caption, value, func);
            return this;
        }
    }

    private transient RobotDashboard dashboard;

    protected long timestamp;
    protected List<Line> lines;
    protected LogImpl log;
    protected transient boolean autoClear;
    protected transient int msTransmissionInterval;
    protected String captionValueSeparator;
    protected String itemSeparator;

    public DashboardTelemetry(RobotDashboard dashboard) {
        this.dashboard = dashboard;
        resetTelemetryForOpMode();
    }

    public void resetTelemetryForOpMode() {
        this.lines = new ArrayList<>();
        this.log = new LogImpl();
        this.autoClear = true;
        this.msTransmissionInterval = 250;
        this.captionValueSeparator = " : ";
        this.itemSeparator = " | ";
    }

    @Override
    public Item addData(String caption, String value, Object... objects) {
        LineImpl line = new LineImpl();
        Item item = line.addData(caption, value, objects);
        this.lines.add(line);
        return item;
    }

    @Override
    public Item addData(String caption, Object value) {
        LineImpl line = new LineImpl();
        Item item = line.addData(caption, value);
        this.lines.add(line);
        return item;
    }

    @Override
    public <T> Item addData(String caption, Func<T> func) {
        LineImpl line = new LineImpl();
        Item item = line.addData(caption, func);
        this.lines.add(line);
        return item;
    }

    @Override
    public <T> Item addData(String caption, String value, Func<T> func) {
        LineImpl line = new LineImpl();
        Item item = line.addData(caption, value, func);
        this.lines.add(line);
        return item;
    }

    @Override
    public boolean removeItem(Item item) {
        for (Line line : lines) {
            if (line instanceof LineImpl && ((LineImpl) line).items.remove(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        this.lines.clear();
    }

    @Override
    public void clearAll() {
        clear();
    }

    @Override
    public Object addAction(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAction(Object action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update() {
        this.timestamp = System.currentTimeMillis();
        dashboard.sendAll(new Message(MessageType.RECEIVE_TELEMETRY, this));
        if (autoClear) {
            clear();
        }
        // TODO
        return true;
    }

    @Override
    public Line addLine() {
        LineImpl line = new LineImpl();
        this.lines.add(line);
        return line;
    }

    @Override
    public Line addLine(String caption) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLine(Line line) {
        return this.lines.remove(line);
    }

    @Override
    public boolean isAutoClear() {
        return this.autoClear;
    }

    @Override
    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    @Override
    public int getMsTransmissionInterval() {
        return msTransmissionInterval;
    }

    @Override
    public void setMsTransmissionInterval(int msTransmissionInterval) {
        this.msTransmissionInterval = msTransmissionInterval;
    }

    @Override
    public String getItemSeparator() {
        return this.itemSeparator;
    }

    @Override
    public void setItemSeparator(String itemSeparator) {
        this.itemSeparator = itemSeparator;
    }

    @Override
    public String getCaptionValueSeparator() {
        return this.captionValueSeparator;
    }

    @Override
    public void setCaptionValueSeparator(String captionValueSeparator) {
        this.captionValueSeparator = captionValueSeparator;
    }

    @Override
    public Log log() {
        return this.log;
    }
}
