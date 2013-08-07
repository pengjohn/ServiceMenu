package com.android.ServiceMenu;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BatteryTest extends Activity implements OnClickListener {
	private String tag = "@@@";

	Timer timer = null;
	Timer timer_charger_status = null;

	private int i = 0;
	private static final String TAG = "BatteryTestActivity";
	private TextView battery_status = null;
	private TextView battery_capability_full = null;
	private TextView battery_capability_current = null;
	private TextView battery_charger_capability = null;
	private TextView battery_discharger_capability = null;
	private TextView battery_voltage = null;
	private TextView battery_current = null;
	private TextView update_time = null;
	private Button startButton;
	private Button endButton;
	private String detal_capability = null;
	private String detal_start_capability = null;
	private String detal_end_capability = null;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int msgId = msg.what;
			Log.e(tag, msgId + "");
			switch (msgId) {
			case 1:
				battery_status.setText(getString(getBatteryStatus()));
				detal_end_capability = getBatteryCapabilityCurrent();
				battery_capability_current.setText(" " + detal_end_capability
						+ " mAs");
				i += 1;
				update_time.setText(" " + i + " sec");
				
				int temp = Integer.parseInt(detal_start_capability)
						- Integer.parseInt(detal_end_capability);
				
				if (temp < 0) {
					// charging
					detal_capability = Integer.toString(-temp);
					battery_charger_capability.setText(" " + detal_capability
							+ " mAs");
					battery_discharger_capability.setText(" 0 mAs");
				} else {
					// discharging
					detal_capability = Integer.toString(-temp);
					battery_discharger_capability.setText(" "
							+ detal_capability + " mAs");
					battery_charger_capability.setText(" 0 mAs");
				}
				battery_voltage.setText(" " + getBatteryVoltage() + " mV");
				battery_current.setText(" " + getBatteryCurrent() + " mA");
				break;
			default:
				break;

			}
		}
	};

	private Handler handler_charger_status = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int msgId = msg.what;
			Log.e(tag, msgId + "");
			switch (msgId) {
			case 1:
				battery_status.setText(getString(getBatteryStatus()));
				//battery_voltage.setText(" " + getBatteryVoltage() + " mV");
				//battery_current.setText(" " + getBatteryCurrent() + " mA");
				break;
			default:
				break;

			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.battery);
		setView();
		setTimerTask_charger_status();
		startButton = (Button) findViewById(R.id.button_start);
		endButton = (Button) findViewById(R.id.button_end);
		startButton.setOnClickListener(this);
		endButton.setOnClickListener(this);
		startButton.setEnabled(true);
		endButton.setEnabled(false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		timer_charger_status.cancel();
		timer_charger_status.purge();
		timer_charger_status = null;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();

		switch (id) {
		case R.id.button_start:
			if (timer == null) {
				setTimerTask();
				detal_start_capability = getBatteryCapabilityCurrent();
				battery_capability_current.setText(" " + detal_start_capability
						+ " mAs");
				battery_charger_capability.setText(" 0 mAs");
				battery_discharger_capability.setText(" 0 mAs");
				startButton.setEnabled(false);
				endButton.setEnabled(true);
			}
			break;
		case R.id.button_end:
			if (timer != null) {
				timer.cancel();
				timer.purge();
				timer = null;
				i = 0;
				detal_end_capability = getBatteryCapabilityCurrent();
				battery_capability_current.setText(" " + detal_end_capability
						+ " mAs");
				update_time.setText(" 0 sec");
				int temp = Integer.parseInt(detal_start_capability)
						- Integer.parseInt(detal_end_capability);
				
				if (temp < 0) {
					// charging
					detal_capability = Integer.toString(-temp);
					battery_charger_capability.setText(" " + detal_capability
							+ " mAs");
					battery_discharger_capability.setText(" 0 mAs");
				} else {
					// discharging
					detal_capability = Integer.toString(-temp);
					battery_discharger_capability.setText(" "
							+ detal_capability + " mAs");
					battery_charger_capability.setText(" 0 mAs");
				}
				startButton.setEnabled(true);
				endButton.setEnabled(false);
			}
			break;
		default:
			Log.e(TAG, "Error!");
			break;
		}
	}

	private void setView() {
		battery_status = (TextView) findViewById(R.id.battery_status);
		battery_status.setText(getString(getBatteryStatus()));

		battery_capability_full = (TextView) findViewById(R.id.battery_capability_full);
		battery_capability_full.setText(" " + getBatteryCapabilityFull()
				+ " mAs");

		battery_capability_current = (TextView) findViewById(R.id.battery_capability_current);
		battery_capability_current.setText(" " + getBatteryCapabilityCurrent()
				+ " mAs");

		battery_charger_capability = (TextView) findViewById(R.id.battery_charger_capability);
		battery_charger_capability.setText(" 0 mAs");

		battery_discharger_capability = (TextView) findViewById(R.id.battery_discharger_capability);
		battery_discharger_capability.setText(" 0 mAs");

		battery_voltage = (TextView) findViewById(R.id.battery_voltage);
		battery_voltage.setText(" " + getBatteryVoltage() + " mV");

		battery_current = (TextView) findViewById(R.id.battery_current);
		battery_current.setText(" " + getBatteryCurrent() + " mA");

		update_time = (TextView) findViewById(R.id.update_time);
		update_time.setText(" 0 sec");
	}

	private void setTimerTask() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}

		}, 0, 1000);
	}

	private void setTimerTask_charger_status() {
		timer_charger_status = new Timer();
		timer_charger_status.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler_charger_status.sendMessage(message);
			}

		}, 0, 1000);
	}

	/**
	 * The contents of the
	 * "/sys/devices/platform/bcmpmu_em/fg_capability_current" file
	 */
	private String getBatteryCapabilityFull() {
		String fg_capability_full = "";
		FileInputStream is = null;
		int count = 0;
		try {
			is = new FileInputStream(
					"/sys/devices/platform/bcmpmu_em/fg_capability_full");
			byte[] buffer = new byte[20];
			count = is.read(buffer);
			if (count > 0) {
				fg_capability_full = new String(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG,
					"No /sys/devices/platform/bcmpmu_em/fg_capability_full ="
							+ e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		Log.e(TAG, "/sys/devices/platform/bcmpmu_em/fg_capability_full ="
				+ fg_capability_full);
		if (count == 0) {
			return "1800";
		}
		return fg_capability_full.substring(0, count - 1);
	}

	/**
	 * The contents of the
	 * "/sys/devices/platform/bcmpmu_em/fg_capability_current" file
	 */
	private String getBatteryCapabilityCurrent() {
		String fg_capability_current = "";
		FileInputStream is = null;
		int count = 0;
		try {
			is = new FileInputStream(
					"/sys/devices/platform/bcmpmu_em/fg_capability_current");
			byte[] buffer = new byte[20];
			count = is.read(buffer);
			if (count > 0) {
				fg_capability_current = new String(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG,
					"No /sys/devices/platform/bcmpmu_em/fg_capability_current ="
							+ e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		Log.e(TAG, "/sys/devices/platform/bcmpmu_em/fg_capability_current ="
				+ fg_capability_current);
		if (count == 0) {
			return "0";
		}
		return fg_capability_current.substring(0, count - 1);
	}

	/**
	 * The contents of the "/sys/class/power_supply/battery/status" file
	 */
	private int getBatteryStatus() {
		String battery_status = "";
		FileInputStream is = null;
		int count = 0;
		try {
			is = new FileInputStream("/sys/class/power_supply/battery/status");
			byte[] buffer = new byte[20];
			count = is.read(buffer);
			if (count > 0) {
				battery_status = new String(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG, "No /sys/class/power_supply/battery/status =" + e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		Log.e(TAG, "/sys/class/power_supply/battery/status=" + battery_status);
		if (battery_status.equals("Charging\n")) {
			return R.string.battery_info_status_charging;
		} else if (battery_status.equals("Full\n")) {
			return R.string.battery_info_status_full;
		} else if (battery_status.equals("Discharging\n")) {
			return R.string.battery_info_status_discharging;
		}
		return R.string.unknown;
	}

	/**
	 * The contents of the "sys/devices/platform/bcmpmu_hwmon/vmbatt" file
	 */
	private String getBatteryVoltage() {
		String BatteryVoltage = "";
		FileInputStream is = null;
		int count = 0;
		try {
			is = new FileInputStream("sys/devices/platform/bcmpmu_hwmon/vmbatt");
			byte[] buffer = new byte[20];
			count = is.read(buffer);
			if (count > 0) {
				BatteryVoltage = new String(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG, "No sys/devices/platform/bcmpmu_hwmon/vmbatt =" + e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		Log.e(TAG, "sys/devices/platform/bcmpmu_hwmon/vmbatt ="
				+ BatteryVoltage);
		if (count == 0) {
			return null;
		}
		return BatteryVoltage.substring(0, count - 1);
	}

	/**
	 * The contents of the "sys/devices/platform/bcmpmu_hwmon/fg_currsmpl" file
	 */
	private String getBatteryCurrent() {
		String BatteryCurrent = "";
		FileInputStream is = null;
		int count = 0;
		try {
			is = new FileInputStream(
					"sys/devices/platform/bcmpmu_hwmon/fg_currsmpl");
			byte[] buffer = new byte[20];
			count = is.read(buffer);
			if (count > 0) {
				BatteryCurrent = new String(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG, "No sys/devices/platform/bcmpmu_hwmon/fg_currsmpl =" + e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		Log.e(TAG, "sys/devices/platform/bcmpmu_hwmon/fg_currsmpl ="
				+ BatteryCurrent);
		if (count == 0) {
			return null;
		}
		return BatteryCurrent.substring(0, count - 1);
	}
}
